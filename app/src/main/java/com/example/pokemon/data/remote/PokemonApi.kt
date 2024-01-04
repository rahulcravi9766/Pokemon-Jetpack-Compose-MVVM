package com.example.pokemon.data.remote

import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.data.remote.response.PokemonDetail
import com.example.pokemon.data.remote.response.PokemonList
import com.example.pokemon.utils.Resources
import retrofit2.Response
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

    /**test api's for pagination */

    @GET("pokemon")
    suspend fun getPokemonListA(
    @Query("offset") offset: Int,
    @Query("limit") limit: Int
    ): PokemonList


}