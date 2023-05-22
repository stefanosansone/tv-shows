package dev.stefanosansone.tvshows.data.network

import dev.stefanosansone.tvshows.data.network.response.ShowsResponse
import dev.stefanosansone.tvshows.utils.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Singleton

/**
 * An interface representing the API endpoint for fetching top rated tv shows.
 */
@Singleton
interface ApiInterface {
    @GET("3/tv/top_rated?api_key=${API_KEY}&language=en-US&page=1")
    suspend fun getShows(): Response<ShowsResponse>
}