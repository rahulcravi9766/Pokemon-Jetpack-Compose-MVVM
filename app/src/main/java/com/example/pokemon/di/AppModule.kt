package com.example.pokemon.di

import android.content.Context
import android.provider.DocumentsContract.Root
import androidx.room.Room
import com.example.pokemon.data.remote.PokemonApi
import com.example.pokemon.database.PokemonDao
import com.example.pokemon.database.PokemonDatabase
import com.example.pokemon.repository.PokemonRepository
import com.example.pokemon.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokemonApi
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokemonApi(): PokemonApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokemonApi::class.java)
    }

    @Singleton
    @Provides
    fun providePokemonDatabase(@ApplicationContext context: Context): PokemonDatabase {

        return Room.databaseBuilder(
            context,
            PokemonDatabase::class.java, "pokemon_database"
        ).build()
    }

    @Singleton
    @Provides
    fun providePokemonDao(database: PokemonDatabase): PokemonDao {
        return database.pokemonDao()
    }
}