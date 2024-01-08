package com.example.pokemon.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.palette.graphics.Palette
import com.example.pokemon.data.models.PokemonListEntry
import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.data.remote.PokemonApi
import com.example.pokemon.database.PokemonDao
import com.example.pokemon.database.RemoteKeysDao
import com.example.pokemon.repository.PokemonRepository
import com.example.pokemon.utils.Constants.PAGE_SIZE
import com.example.pokemon.utils.PokemonRemoteMediator
import com.example.pokemon.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val pokemonDao: PokemonDao
) : ViewModel() {

    private var curPage = 0
    var pokemonList = mutableStateOf<List<PokemonListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)
    var pokemonListToCache = listOf<PokemonListToCache>()
    var searchedPokemonList = mutableStateOf<Flow<PagingData<PokemonListToCache>>>(flowOf())

//    private val _pagingDataFlow = MutableStateFlow<PagingData<PokemonListToCache>>(PagingData.empty())
//    var pagingDataFlow: Flow<PagingData<PokemonListToCache>> = _pagingDataFlow


    init {
        //  loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(curPage * PAGE_SIZE, PAGE_SIZE)
            Timber.tag("result").d("success ${result.data}")
            when (result) {
                is Resources.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data?.count!!
                    val pokemonListEntry = result.data.results?.mapIndexed { index, entry ->
                        val number = if (entry?.url?.endsWith("/") == true) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry?.url?.takeLastWhile { it.isDigit() }
                        }

                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokemonListEntry(entry?.name!!.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        }, url, number?.toInt() ?: 0)
                    } ?: listOf()


//                    pokemonListToCache = pokemonListEntry.mapIndexed { index, it ->
//                        PokemonListToCache(
//                            pokemonName = it.pokemonName,
//                            imageUrl = it.imageUrl,
//                            number = it.number,
//                            page = 0
//                        )
//                    }

                    Log.d("pokemonListToCache", "is $pokemonListToCache")
                    //  pokemonDao.insertAllPokemon(pokemonListToCache)

                    curPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokemonListEntry
                }

                is Resources.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
            }
        }
    }

    suspend fun getAllPokemon(): List<PokemonListToCache> {
        return pokemonDao.getAllPokemon()
    }

//    suspend fun searchPokemonByName(name: String): List<PokemonListToCache> {
//        return withContext(Dispatchers.IO) {
//            pokemonDao.searchPokemonByName(name)
//        }
//    }

    fun calculateDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {

        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    fun getPagedPokemon(): Flow<PagingData<PokemonListToCache>> =
        repository.getPagedPokemon().cachedIn(viewModelScope)


    fun getSearchedPokemon(name: String) {
        searchedPokemonList.value = repository.getSearchedPokemon(name).cachedIn(viewModelScope)
    }

    companion object{
        const val TAG = "ViewModel"
    }

}