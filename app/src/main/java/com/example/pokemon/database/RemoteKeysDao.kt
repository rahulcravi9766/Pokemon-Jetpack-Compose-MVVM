package com.example.pokemon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokemon.data.models.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys_table")
    suspend fun getRemoteKeys(): List<RemoteKeys?>

    @Query("DELETE FROM remote_keys_table")
    suspend fun deleteRemoteKeys()

    @Query("SELECT created_at FROM remote_keys_table ORDER BY created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}