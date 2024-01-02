package com.example.pokemon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list_table")
data class PokemonListToCache(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pokemonName: String,
    val imageUrl: String,
    val number: Int
)
