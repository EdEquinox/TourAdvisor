package pt.isec.touradvisor.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import pt.isec.touradvisor.utils.firebase.FAuthUtil
import pt.isec.touradvisor.utils.firebase.FStorageUtil

data class User(val name: String, val email: String, val picture:String?)

fun FirebaseUser.toUser() : User{
    val displayName = this.displayName ?: ""
    val email = this.email ?: ""
    val picture = this.photoUrl.toString() ?:""

    return User(displayName, email, picture)
}

class FirebaseViewModel : ViewModel() {

    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user : MutableState<User?>
        get() = _user

    private val _error = mutableStateOf<String?>(null)
    val error: MutableState<String?>
        get() = _error

    fun createUserWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank())
            return

        viewModelScope.launch { //serve para nao haver destruição de dados caso a aplicação seja destruida a meio da atividade
            FAuthUtil.createUserWithEmail(email, password) { exception ->
                if (exception == null)
                    _user.value = FAuthUtil.currentUser?.toUser()
                _error.value = exception?.message
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank())
            return
        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email, password) { exception ->
                if (exception == null)
                    _user.value = FAuthUtil.currentUser?.toUser()
                _error.value = exception?.message
            }
        }
    }

    fun signOut() {
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }

    private val _nrgames = mutableLongStateOf(0L)
    val nrgames : MutableState<Long>
        get() = _nrgames

    private val _topscore = mutableLongStateOf(0L)
    val topscore : MutableState<Long>
        get() = _topscore

    fun addDataToFirestore() {
        viewModelScope.launch {
            FStorageUtil.addDataToFirestore { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun updateDataInFirestore() {
        viewModelScope.launch {
            //FirebaseUtils.updateDataInFirestore()
            FStorageUtil.updateDataInFirestoreTrans { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun removeDataFromFirestore() {
        viewModelScope.launch {
            FStorageUtil.removeDataFromFirestore { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun startObserver() {
        viewModelScope.launch {
            FStorageUtil.startObserver { g, t ->
                _nrgames.longValue = g
                _topscore.longValue = t
            }
        }
    }

    fun stopObserver() {
        viewModelScope.launch {
            FStorageUtil.stopObserver()
        }
    }
}
