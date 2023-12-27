package pt.isec.touradvisor.utils.firebase

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FAuthUtil {
    companion object {
        private val auth by lazy { Firebase.auth }

        val currentUser: FirebaseUser? // se for null n ha ninguem autenticado
            get() = auth.currentUser

        fun createUserWithEmail(email: String, password: String, onResult: (Throwable?) -> Unit) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun signInWithEmail(email: String, password: String, onResult: (Throwable?) -> Unit) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun signOut() {
            if (auth.currentUser != null) {
                auth.signOut()
            }
        }

        fun changePassword(oldPassword: String, newPassword: String, newPassword2: String, onResult: (Throwable?) -> Unit) {
            if (oldPassword == newPassword2) {
                Log.i("TAG", "New password can't be the same as the old one")
                onResult(Throwable("New password can't be the same as the old one"))
                return
            }
            if (newPassword != newPassword2) {
                Log.i("TAG", "Passwords don't match")
                onResult(Throwable("Passwords don't match"))
                return
            }
            if (oldPassword.isBlank() || newPassword.isBlank() || newPassword2.isBlank()) {
                Log.i("TAG", "Fields can't be empty")
                onResult(Throwable("Fields can't be empty"))
                return
            }
            if (oldPassword.length < 6 || newPassword.length < 6 || newPassword2.length < 6) {
                Log.i("TAG", "Password must be at least 6 characters long")
                onResult(Throwable("Password must be at least 6 characters long"))
                return
            }
            if(reauthenticate(oldPassword).value) {
                Log.i("TAG", "Wrong password")
                onResult(Throwable("Wrong password"))
                return
            }

            if (auth.currentUser != null) {
                Log.i("TAG", "Password changed")
                auth.currentUser!!.updatePassword(newPassword).addOnSuccessListener { onResult(null) }.addOnFailureListener { onResult(it) }
            }
        }

        fun changeEmail(newEmail: String, onResult: (Throwable?) -> Unit) {

            if (newEmail.isBlank()) {
                Log.i("TAG", "Fields can't be empty")
                onResult(Throwable("Fields can't be empty"))
                return
            }


            if (auth.currentUser != null) {
                Log.i("TAG", "Email changed")
                auth.currentUser!!.updateEmail(newEmail).addOnSuccessListener { onResult(null) }.addOnFailureListener { onResult(it) }
            }
        }

        fun changeNickname(nickname: String, onResult: (Throwable?) -> Unit) {
            if (nickname.isBlank()) {
                Log.i("TAG", "Nickname can't be empty")
                onResult(Throwable("Nickname can't be empty"))
                return
            }
            if (nickname.length < 6) {
                Log.i("TAG", "Nickname must be at least 6 characters long")
                onResult(Throwable("Nickname must be at least 6 characters long"))
                return
            }

            if (auth.currentUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()

                auth.currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("TAG", "Nickname changed")
                            onResult(null)
                        } else {
                            onResult(task.exception)
                        }
                    }
            }
        }

        fun deleteAccount(password: String, onResult: (Throwable?) -> Unit) {
            if (password.isBlank()) {
                Log.i("TAG", "Password can't be empty")
                onResult(Throwable("Password can't be empty"))
                return
            }
            if(reauthenticate(password).value) {
                Log.i("TAG", "Wrong password")
                onResult(Throwable("Wrong password"))
                return
            }

            if (auth.currentUser != null) {
                auth.currentUser!!.delete().addOnSuccessListener { onResult(null) }.addOnFailureListener { onResult(it) }
            }
        }

        private fun reauthenticate(oldPassword: String): MutableState<Boolean> {
            val credential = EmailAuthProvider.getCredential(auth.currentUser!!.email!!, oldPassword)
            val boolean = mutableStateOf(false)
            currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                boolean.value = !it.isSuccessful
            }
            return boolean
        }
    }
}