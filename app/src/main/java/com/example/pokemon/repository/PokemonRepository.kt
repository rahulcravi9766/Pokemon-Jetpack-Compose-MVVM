package com.example.pokemon.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.data.remote.PokemonApi
import com.example.pokemon.data.remote.response.PokemonDetail
import com.example.pokemon.data.remote.response.PokemonList
import com.example.pokemon.database.PokemonDao
import com.example.pokemon.database.RemoteKeysDao
import com.example.pokemon.utils.Constants
import com.example.pokemon.utils.PokemonRemoteMediator
import com.example.pokemon.utils.Resources
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokemonApi,
    private val pokemonDao: PokemonDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val dispatcher: CoroutineDispatcher
){

    suspend fun getPokemonList(offset: Int, limit: Int): Resources<PokemonList>{
        val response = try {
            api.getPokemonList(offset, limit)
        }catch (e: Exception){
            return Resources.Error(message = e.message ?: "")
        }
        return Resources.Success(response)
    }

    suspend fun getPokemonDetails(name: String): Resources<PokemonDetail>{
        val response = try {
            api.getPokemonDetails(name)
        }catch (e: Exception){
            return Resources.Error(message = e.message ?: "")
        }
        return Resources.Success(response)
    }

//    suspend fun getPokemonListA(offset: Int, limit: Int): Resources<PokemonList>{
//        val response = try {
//            api.getPokemonListA(offset, limit)
//        }catch (e: Exception){
//            return Resources.Error(message = e.message ?: "")
//        }
//        return Resources.Success(response)
//    }

    @OptIn(ExperimentalPagingApi::class)
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