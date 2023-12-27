package pt.isec.touradvisor.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
import pt.isec.touradvisor.data.User
import pt.isec.touradvisor.data.toUser
import pt.isec.touradvisor.utils.firebase.FAuthUtil
import pt.isec.touradvisor.utils.firebase.FStorageUtil
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseViewModel : ViewModel() {

    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user : MutableState<User?>
        get() = _user

    private val _error = mutableStateOf<String?>(null)
    val error: MutableState<String?>
        get() = _error

    private val _userUID = mutableStateOf(FAuthUtil.currentUser?.uid)
    val userUID : MutableState<String?>
        get() = _userUID

    private val _myPOIs = mutableStateOf(listOf<POI>())
    val myPOIs : MutableState<List<POI>>
        get() = _myPOIs

    private val _sortedPOIs = mutableStateOf(listOf<POI>())
    val sortedPOIs : MutableState<List<POI>>
        get() = _sortedPOIs

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

    private val _categories = mutableStateOf(listOf<Category>())
    val categories : MutableState<List<Category>>
        get() = _categories

    private val _POIs = mutableStateOf(listOf<POI>())
    val POIs : MutableState<List<POI>>
        get() = _POIs

    private val _locations = mutableStateOf(listOf<Local>())
    val locations : MutableState<List<Local>>
        get() = _locations

    fun addPOIToFirestore(data: HashMap<String,Any>) {
        viewModelScope.launch {
            FStorageUtil.addPOIToFirestore(data) { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun addCategoryToFirestore(data: HashMap<String,Any>) {
        viewModelScope.launch {
            FStorageUtil.addCategoryToFirestore(data) { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun addLocationToFirestore(data: HashMap<String,Any>) {
        viewModelScope.launch {
            FStorageUtil.addLocationToFirestore(data) { exception ->
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

    suspend fun startObserver() : Boolean {
        return suspendCoroutine { continuation ->
            FStorageUtil.startObserver(onNewValues = { c, p, l ->
                _categories.value = c
                _POIs.value = p
                _locations.value = l
            }, onReady = {
                continuation.resume(true)
            })
        }

    }

    suspend fun getUserPOIs(): List<POI> {
        return suspendCoroutine { continuation ->
            userUID.value?.let {
                FStorageUtil.getUserPOIS(it) { pois->
                    myPOIs.value = pois
                    sortedPOIs.value = pois
                    continuation.resume(pois)
                }
            }
        }
    }

    fun stopObserver() {
        viewModelScope.launch {
            FStorageUtil.stopObserver()
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, newPassword2: String) {
        viewModelScope.launch {
            FAuthUtil.changePassword(oldPassword, newPassword, newPassword2) { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun changeEmail(newEmail: String) {
        viewModelScope.launch {
            FAuthUtil.changeEmail(newEmail) { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun changeNick(nickname: String) {
        viewModelScope.launch {
            FAuthUtil.changeNickname(nickname) { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            FAuthUtil.deleteAccount(password) { exception ->
                _error.value = exception?.message
            }
        }
    }
}
