package dev.stefanosansone.tvshows.data.model

import dev.stefanosansone.tvshows.utils.IMAGE_BASE_URL
import org.junit.Assert.assertEquals
import org.junit.Test

class TvShowTest {

    private val POSTER_PATH = "/xUfRZu2mi8jH6SzQEJGP6tjBuYj.jpg"

    @Test
    fun testGetFullPosterPath() {
        val tvShow = TvShow(IMAGE_BASE_URL, POSTER_PATH)
        val expected = IMAGE_BASE_URL + POSTER_PATH
        val actual = tvShow.getFullPosterPath()
        assertEquals(expected, actual)
    }
}
