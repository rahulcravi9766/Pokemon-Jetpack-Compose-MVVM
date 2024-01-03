package com.example.pokemon.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokemon.data.models.PokemonListEntry
import com.example.pokemon.data.models.PokemonListToCache

@Dao
interface  PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPokemon(pokemonList: List<PokemonListToCache>)

    @Query("SELECT * FROM pokemon_list_table")
    suspend fun getAllPokemon(): List<PokemonListToCache>

    @Query("SELECT * FROM pokemon_list_table WHERE pokemonName LIKE '%' || :name || '%'")
    fun searchPokemonByName(name: String): List<PokemonListToCache>

}