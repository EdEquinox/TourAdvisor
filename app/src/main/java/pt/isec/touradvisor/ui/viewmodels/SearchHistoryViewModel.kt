package pt.isec.touradvisor.ui.viewmodels

import androidx.lifecycle.ViewModel

class SearchHistoryViewModel : ViewModel(){

    private val _searchHistory = mutableListOf<String>()

    val searchHistory : List<String>
        get() = _searchHistory

    fun addSearch(search: String) {
        if (_searchHistory.contains(search)) {
            _searchHistory.remove(search)
        }
        _searchHistory.add(0, search)
    }

    fun removeSearch(search: String) {
        _searchHistory.remove(search)
    }

    fun clearSearchHistory() {
        _searchHistory.clear()
    }
}