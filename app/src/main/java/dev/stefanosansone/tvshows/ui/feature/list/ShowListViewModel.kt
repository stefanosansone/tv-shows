package dev.stefanosansone.tvshows.ui.feature.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stefanosansone.tvshows.data.model.TvShow
import dev.stefanosansone.tvshows.data.repository.ShowRepository
import dev.stefanosansone.tvshows.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowListViewModel @Inject constructor(
    private val showRepository: ShowRepository
): ViewModel() {

    private val _showsUiState: MutableStateFlow<ShowListUiState> = MutableStateFlow(ShowListUiState.Loading)
    val showsUiState: StateFlow<ShowListUiState> = _showsUiState

    private val _sortOrder = MutableStateFlow(ShowListSortType.UNSORTED)
    val sortOrder: StateFlow<ShowListSortType> = _sortOrder

    private val _originalShows = mutableListOf<TvShow>()

    init {
        viewModelScope.launch {
            fetchShows()
        }
    }

    /**
     * Fetches the show list from the repository, caches the original, and saves a sorted version in the UI state.
     */
    @VisibleForTesting
    suspend fun fetchShows() {
        showRepository.getShows().map { result ->
            when (result) {
                is NetworkResult.Success -> {
                    _originalShows.clear()
                    _originalShows.addAll(result.data)
                    _showsUiState.value = ShowListUiState.Success(_originalShows, _sortOrder.value)
                    sortShows()
                }
                is NetworkResult.Error -> {
                    _showsUiState.value = ShowListUiState.Error(result.message)
                }
            }
        }.collect()
    }

    /**
     * Sets the sort type for the show list and reorders the saved list.
     * @param showListSortType An enum representing the sorting type.
     */
    fun setSorting(showListSortType: ShowListSortType) {
        _sortOrder.value = showListSortType
        sortShows()
    }

    /**
     * Sorts the saved list in the UI state if the fetch was successful.
     */
    private fun sortShows() {
        val sortedShows = when (_sortOrder.value) {
            ShowListSortType.NAME -> _originalShows.sortedBy { it.name }
            ShowListSortType.UNSORTED -> _originalShows
        }
        when (_showsUiState.value) {
            is ShowListUiState.Success -> {
                _showsUiState.value = ShowListUiState.Success(sortedShows, _sortOrder.value)
            }
            else -> { }
        }
    }

    fun retry() {
        viewModelScope.launch {
            fetchShows()
        }
    }

}