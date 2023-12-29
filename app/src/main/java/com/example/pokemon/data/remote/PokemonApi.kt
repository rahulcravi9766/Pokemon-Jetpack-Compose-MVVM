package com.example.pokemon.data.remote

import com.example.pokemon.data.remote.response.PokemonDetail
import com.example.pokemon.data.remote.response.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {

    //"https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String
    ): PokemonDetail

}