package dev.stefanosansone.tvshows.data.repository

import dev.stefanosansone.tvshows.data.network.ApiInterface
import dev.stefanosansone.tvshows.data.network.response.ShowsResponse
import dev.stefanosansone.tvshows.data.repository.impl.ShowRepositoryImpl
import dev.stefanosansone.tvshows.utils.IMAGE_BASE_URL
import dev.stefanosansone.tvshows.utils.NetworkResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ShowRepositoryImplTest {
    @MockK
    lateinit var apiInterface: ApiInterface

    lateinit var repository: ShowRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ShowRepositoryImpl(apiInterface)
    }

    @Test
    fun `getShows success`() = runTest {
        val showsResponse = ShowsResponse(
            page = 1,
            results = listOf(
                ShowsResponse.Result(
                    backdropPath = "/test_backdrop_path.jpg",
                    firstAirDate = "2023-01-01",
                    genreIds = listOf(1,2,3),
                    id = 1001,
                    name = "Test Show",
                    originCountry = listOf("US"),
                    originalLanguage = "en",
                    originalName = "Test Show Original",
                    overview = "Test overview",
                    popularity = 888.999,
                    posterPath = "/test_poster_path.jpg",
                    voteAverage = 1.2,
                    voteCount = 12345
                )
            ),
            totalPages = 142,
            totalResults =  2839
        )
        every { runBlocking { apiInterface.getShows() } } returns Response.success(showsResponse)


        val result = repository.getShows().first()


        assertTrue(result is NetworkResult.Success)
        val shows = (result as NetworkResult.Success).data
        assertEquals(1, shows.size)
        assertEquals("Test Show", shows[0].name)
        assertEquals(IMAGE_BASE_URL + shows[0].posterPath, shows[0].getFullPosterPath())
    }

    @Test
    fun `getShows error`() = runTest {
        every { runBlocking { apiInterface.getShows() } } returns Response.error(
            404,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )

        val result = repository.getShows().first()

        assertTrue(result is NetworkResult.Error)
    }
}
