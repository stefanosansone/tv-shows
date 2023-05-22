package dev.stefanosansone.tvshows.data.repository.impl

import dev.stefanosansone.tvshows.data.model.TvShow
import dev.stefanosansone.tvshows.data.network.ApiInterface
import dev.stefanosansone.tvshows.data.repository.ShowRepository
import dev.stefanosansone.tvshows.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ShowRepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface,
) : ShowRepository {

    override fun getShows(): Flow<NetworkResult<List<TvShow>>> = flow {
        val response = apiInterface.getShows()
        if (response.isSuccessful) {
            val shows = response.body()?.results?.map {
                TvShow(
                    name = it.name,
                    posterPath = it.posterPath
                )
            }?: emptyList()
            emit(NetworkResult.Success(shows))
        } else {
            emit(NetworkResult.Error(response.message()))
        }
    }.catch { e ->
        emit(NetworkResult.Error(e.localizedMessage?: "Error while fetching data"))
    }.flowOn(Dispatchers.IO)
}