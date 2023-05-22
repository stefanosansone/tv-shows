package dev.stefanosansone.tvshows.data.repository

import dev.stefanosansone.tvshows.data.model.TvShow
import dev.stefanosansone.tvshows.utils.NetworkResult
import kotlinx.coroutines.flow.Flow


interface ShowRepository {
    /**
     * Gets the top rated tv shows as a flow
     */
    fun getShows(): Flow<NetworkResult<List<TvShow>>>
}