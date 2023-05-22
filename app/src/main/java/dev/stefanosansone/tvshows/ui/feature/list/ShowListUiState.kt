package dev.stefanosansone.tvshows.ui.feature.list

import dev.stefanosansone.tvshows.data.model.TvShow

/**
 * Sealed interface that represents the possible UI states of the TV show list screen.
 */
sealed interface ShowListUiState {
    data class Success(val shows: List<TvShow>, val order: ShowListSortType) : ShowListUiState
    data class Error(val message: String) : ShowListUiState
    object Loading : ShowListUiState
}