package com.example.pokemon.utils

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.data.models.RemoteKeys
import com.example.pokemon.data.remote.PokemonApi
import com.example.pokemon.database.PokemonDao
import com.example.pokemon.database.RemoteKeysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//https://towardsdev.com/pagination-in-android-using-paging-3-and-room-database-for-caching-e226ff8db337
@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val api: PokemonApi,
    private val dispatcher: CoroutineDispatcher
) : RemoteMediator<Int, PokemonListToCache>() {

    override suspend fun initialize(): InitializeAction {
        /**48 hour validity period for the cached data*/
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(48, TimeUnit.HOURS)

        return withContext(dispatcher) {
            if (System.currentTimeMillis() - (remoteKeysDao.getCreationTime()
                    ?: 0) < cacheTimeout
            ) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonListToCache>
    ): MediatorResult {
        TODO("Not yet implemented")
    }

    private suspend fun getRemoteKeyForFirstTime(): RemoteKeys? {
        return withContext(dispatcher) {
            remoteKeysDao.getRemoteKeys().firstOrNull()
        }
    }

    private suspend fun getRemoteKeyForLastTime(): RemoteKeys? {
        return withContext(dispatcher) {
            remoteKeysDao.getRemoteKeys().lastOrNull()
        }
    }
}