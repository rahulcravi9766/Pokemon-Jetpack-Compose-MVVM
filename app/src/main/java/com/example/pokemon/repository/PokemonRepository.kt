package com.example.pokemon.repository

import com.example.pokemon.data.remote.PokemonApi
import com.example.pokemon.data.remote.response.PokemonDetail
import com.example.pokemon.data.remote.response.PokemonList
import com.example.pokemon.utils.Resources
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokemonApi
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

}