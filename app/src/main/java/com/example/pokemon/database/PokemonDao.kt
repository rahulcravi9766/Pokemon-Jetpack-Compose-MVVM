package com.example.pokemon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pokemon.data.models.PokemonListToCache

@Dao
interface  PokemonDao {

    @Insert
    suspend fun insertAllPokemon(pokemonList: PokemonListToCache)

//    @Query("SELECT * FROM pokemon_list_table")
//    suspend fun getAllPokemon(): List<PokemonListToCache>

}