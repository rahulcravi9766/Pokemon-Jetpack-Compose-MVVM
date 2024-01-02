package com.example.pokemon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokemon.data.models.PokemonListToCache

@Database(entities = [PokemonListToCache::class], version = 1)
abstract class PokemonDatabase: RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao

}