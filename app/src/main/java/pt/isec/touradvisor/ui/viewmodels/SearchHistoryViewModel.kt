package pt.isec.touradvisor.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pt.isec.touradvisor.data.Category
import pt.isec.touradvisor.data.Local
import pt.isec.touradvisor.data.POI

class SearchHistoryViewModel : ViewModel() {

    private val _searchHistory = mutableListOf<String>()
    private val _searchedPOIs = mutableStateOf(listOf<POI>())
    private val _searchedLocals = mutableStateOf(listOf<Local>())
    private val _searchedCategories = mutableStateOf(listOf<Category>())

    val searchedPOIs: MutableState<List<POI>>
        get() = _searchedPOIs

    val searchedLocals: MutableState<List<Local>>
        get() = _searchedLocals

    val searchedCategories: MutableState<List<Category>>
        get() = _searchedCategories


    val searchHistory: List<String>
        get() = _searchHistory

    fun addSearch(search: String) {
        if (_searchHistory.contains(search)) {
            _searchHistory.remove(search)
        }
        _searchHistory.add(0, search)
    }

}