package com.example.pokemon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokemon.data.models.PokemonListToCache
import com.example.pokemon.data.models.RemoteKeys

@Database(entities = [PokemonListToCache::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class PokemonDatabase: RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}