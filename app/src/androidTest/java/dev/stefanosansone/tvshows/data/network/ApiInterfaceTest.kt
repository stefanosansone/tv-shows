package dev.stefanosansone.tvshows.data.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiInterfaceTest {
    private lateinit var apiInterface: ApiInterface

    @Before
    fun setup() {
        val apiService = ApiService()
        apiInterface = apiService.providesTmdbApi()
    }

    @Test
    fun testGetShows() {
        runBlocking {
            val response = apiInterface.getShows()
            assertTrue(response.isSuccessful)
            assertNotNull(response.body())
            assertEquals(1, response.body()!!.page)
            assertTrue(response.body()!!.results.isNotEmpty())
        }
    }
}
