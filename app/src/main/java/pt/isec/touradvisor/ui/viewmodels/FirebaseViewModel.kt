package pt.isec.touradvisor.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import pt.isec.touradvisor.data.Avaliacao
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

    var logged = mutableStateOf(false)

    private val _sortedLocal = mutableStateOf(listOf<Local>())
    val sortedLocal: MutableState<List<Local>>
        get() = _sortedLocal

    private val _rawUser = mutableStateOf(FAuthUtil.currentUser)
    val rawUser : MutableState<FirebaseUser?>
        get() = _rawUser

    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user : MutableState<User?>
        get() = _user

    fun getNickname() : String? {
        return rawUser.value?.displayName
    }

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

    private val _myRatings = mutableStateOf(listOf<Avaliacao>())
    val myRatings : MutableState<List<Avaliacao>>
        get() = _myRatings

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

    private val _myPfp = mutableStateOf("")
    val myPfp : MutableState<String>
        get() = _myPfp

    private val _searchedPOIs = mutableStateOf(listOf<POI>())
    private val _searchedLocations = mutableStateOf(listOf<Local>())
    private val _searchedCategories = mutableStateOf(listOf<Category>())

    val searchedPOIs : MutableState<List<POI>>
        get() = _searchedPOIs

    val searchedLocations : MutableState<List<Local>>
        get() = _searchedLocations

    val searchedCategories : MutableState<List<Category>>
        get() = _searchedCategories

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

    fun addPFPToFirestore(user: String, newPFP: String) {
        viewModelScope.launch {
            FStorageUtil.addPFPToFirestore(user,newPFP) { exception ->
                _error.value = exception?.message
            }
        }
    }

    suspend fun startObserver() : Boolean {
        Log.i("OBSERVER", "start")
        return suspendCoroutine { continuation ->
            Log.i("OBSERVER", "start2")
            FStorageUtil.startObserver(onNewValues = { c, p, l ->
                Log.i("OBSERVER", "start3")
                try {
                    if (userUID.value == null){
                        Log.i("OBSERVER", "null")
                        continuation.resume(true)
                    } else{
                        Log.i("OBSERVER", "not null")
                        _categories.value = c
                        _POIs.value = p
                        _locations.value = l
                        _sortedLocal.value = l
                        continuation.resume(true)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, onReady = {
            })
        }

    }

    suspend fun getUserPOIs(): List<POI> {
        return suspendCoroutine { continuation ->
            userUID.value?.let {
                FStorageUtil.getUserPOIS(it) { pois->
                    try {
                        if (userUID.value == null){
                            Log.i("POIS", "null")
                            continuation.resume(listOf())
                            return@getUserPOIS
                        }
                        myPOIs.value = pois
                        sortedPOIs.value = pois
                        continuation.resume(pois)
                    } catch (e: Exception) {
                       e.printStackTrace()
                    }
                }
            }
        }
    }

    suspend fun getUserPFP(user: String) {
        return suspendCoroutine {
            FStorageUtil.getUserPfp(user) { pfp ->
                try {
                    if (userUID.value == null){
                        Log.i("PFP", "null")
                        it.resume(Unit)
                        return@getUserPfp
                    }
                    myPfp.value = pfp
                    Log.i("PFP", myPfp.value)
                    it.resume(Unit)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getUserRatings(user: String) {
        return suspendCoroutine {
            FStorageUtil.getUserRatings(user) { ratings ->
                try {
                    myRatings.value = ratings
                } catch (e: Exception) {
                    e.printStackTrace()
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

    fun addAvaliacao(avaliacao: Avaliacao) {
        viewModelScope.launch {
            FStorageUtil.addAvaliacao(avaliacao) { exception ->
                _error.value = exception?.message
            }
        }

    }

    fun removePoiFromFirestore(name: String) {
        viewModelScope.launch {
            FStorageUtil.removePoiFromFirestore(name) { exception ->
                _error.value = exception?.message
            }
        }
    }
}
