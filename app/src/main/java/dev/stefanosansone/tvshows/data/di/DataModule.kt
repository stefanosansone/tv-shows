package dev.stefanosansone.tvshows.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.stefanosansone.tvshows.data.repository.ShowRepository
import dev.stefanosansone.tvshows.data.repository.impl.ShowRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindShowRepository(
        showRepository: ShowRepositoryImpl,
    ): ShowRepository
}
