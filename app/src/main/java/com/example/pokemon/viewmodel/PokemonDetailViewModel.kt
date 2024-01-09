package com.example.pokemon.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pokemon.data.remote.response.PokemonDetail
import com.example.pokemon.repository.PokemonRepository
import com.example.pokemon.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel(){

    suspend fun getPokemonDetails(pokemonName: String): Resources<PokemonDetail>{
        return repository.getPokemonDetails(pokemonName)
    }
}