package com.example.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.MineralEntity
import com.example.data.repository.MineralRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchFilterState(
    val query: String = "",
    val selectedColor: String = "All",
    val selectedLuster: String = "All",
    val selectedClassification: String = "All",
    val minHardness: Float = 1.0f,
    val maxHardness: Float = 10.0f
)

class SearchViewModel(private val repository: MineralRepository) : ViewModel() {

    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<MineralEntity>> = _filterState
        .flatMapLatest { filter ->
            repository.searchMinerals(
                query = filter.query,
                colorCategory = filter.selectedColor,
                luster = filter.selectedLuster,
                classification = filter.selectedClassification,
                minHardness = filter.minHardness.toDouble(),
                maxHardness = filter.maxHardness.toDouble()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChange(newQuery: String) {
        _filterState.value = _filterState.value.copy(query = newQuery)
    }

    fun onColorSelected(color: String) {
        _filterState.value = _filterState.value.copy(selectedColor = color)
    }

    fun onLusterSelected(luster: String) {
        _filterState.value = _filterState.value.copy(selectedLuster = luster)
    }

    fun onClassificationSelected(classification: String) {
        _filterState.value = _filterState.value.copy(selectedClassification = classification)
    }

    fun onHardnessRangeChanged(min: Float, max: Float) {
        _filterState.value = _filterState.value.copy(
            minHardness = min.coerceIn(1.0f, 10.0f),
            maxHardness = max.coerceIn(1.0f, 10.0f)
        )
    }

    fun resetFilters() {
        _filterState.value = SearchFilterState()
    }

    fun toggleFavorite(mineralId: Int, currentFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFavorite(mineralId, !currentFavorite)
        }
    }

    class Factory(private val repository: MineralRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(repository) as T
        }
    }
}
