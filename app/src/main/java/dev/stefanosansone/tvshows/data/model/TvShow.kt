package dev.stefanosansone.tvshows.data.model

import dev.stefanosansone.tvshows.utils.IMAGE_BASE_URL

data class TvShow(
    val name: String,
    val posterPath: String
){
    fun getFullPosterPath(): String {
        return IMAGE_BASE_URL + posterPath
    }
}
