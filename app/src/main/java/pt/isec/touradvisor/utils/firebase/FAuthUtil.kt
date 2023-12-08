package pt.isec.touradvisor.utils.firebase

import com.google.firebase.auth.FirebaseUser
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
    }
}