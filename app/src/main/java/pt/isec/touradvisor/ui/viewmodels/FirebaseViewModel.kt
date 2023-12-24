package pt.isec.touradvisor.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI
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

    private val _userUID = mutableStateOf(FAuthUtil.currentUser?.uid)
    val userUID : MutableState<String?>
        get() = _userUID

    private val _myPOIs = mutableStateOf(listOf<POI>())
    val myPOIs : MutableState<List<POI>>
        get() = _myPOIs

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

    fun getUserPOIs(){
        viewModelScope.launch {
            userUID.value?.let {
                FStorageUtil.getUserPOIS(it) { pois->
                    myPOIs.value = pois
                }
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
            FStorageUtil.startObserver { c, p, l ->
                _categories.value = c
                _POIs.value = p
                _locations.value = l
            }
        }
    }

    fun stopObserver() {
        viewModelScope.launch {
            FStorageUtil.stopObserver()
        }
    }
}
