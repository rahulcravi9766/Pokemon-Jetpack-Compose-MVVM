package com.example.pokemon.utils

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.data.models.RemoteKeys
import com.example.pokemon.data.remote.PokemonApi
import com.example.pokemon.database.PokemonDao
import com.example.pokemon.database.RemoteKeysDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
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

        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyForFirstTime()
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastTime()
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        return withContext(dispatcher) {
            try {
                val response = api.getPokemonListA(offset = page * 20, limit = 20)

                val pokemonList = response.results
                val endOfPagination = pokemonList?.isEmpty()


                        if (pokemonList?.isNotEmpty() == true) {
                            if (loadType == LoadType.REFRESH) {
                                remoteKeysDao.deleteRemoteKeys()
                                pokemonDao.deleteAllPokemon()
                            }
                            val prevKey = if (page > 1) page - 1 else null
                            val nextKey = if (endOfPagination!!) null else page + 1
                            val remoteKeys = pokemonList.mapIndexed { _, item ->
                                 val  number = if (item?.url?.endsWith("/") == true) {
                                    item.url.dropLast(1).takeLastWhile { it.isDigit() }
                                } else {
                                     item?.url?.takeLastWhile { it.isDigit() }
                                 }
                                RemoteKeys(
                                    id = number?.toInt() ?: 0,
                                    prevKey = prevKey,
                                    currentPage = page,
                                    nextKey = nextKey
                                )
                            }
                            val pokemonListEntry = pokemonList.mapIndexed { _, item ->
                                val number = if (item?.url?.endsWith("/") == true) {
                                    item.url.dropLast(1).takeLastWhile { it.isDigit() }
                                } else {
                                    item?.url?.takeLastWhile { it.isDigit() }
                                }
                                val url =
                                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                                PokemonListToCache(pokemonName = item?.name!!.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.ROOT
                                    ) else it.toString()
                                }, imageUrl = url, number = number?.toInt() ?: 0, page = page)
                            }
                            remoteKeysDao.insertAll(remoteKeys)
                            pokemonDao.insertAllPokemon(pokemonListEntry)
                        }

                MediatorResult.Success(endOfPaginationReached = endOfPagination!!)

            } catch (error: IOException) {
                MediatorResult.Error(error)
            } catch (error: HttpException) {
                MediatorResult.Error(error)
            }
        }
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


    fun getPagedHouses(): Flow<PagingData<PokemonListToCache>> {
        return Pager(
            initialKey = 1,
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                prefetchDistance = Constants.PAGE_SIZE / 4,
                initialLoadSize = Constants.PAGE_SIZE
            ),
            pagingSourceFactory = {
                pokemonDao.getAllPokemonByPageOrder()
            },
            remoteMediator = PokemonRemoteMediator(
                pokemonDao = pokemonDao,
                remoteKeysDao = remoteKeysDao,
                api = api,
                dispatcher = dispatcher
            )
        ).flow
    }
}