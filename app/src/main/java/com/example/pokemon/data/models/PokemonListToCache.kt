package com.example.pokemon.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_list_table")
data class PokemonListToCache(
    @PrimaryKey(autoGenerate = false)
    var number: Int,
    var pokemonName: String,
    var imageUrl: String,
    var page: Int

)
