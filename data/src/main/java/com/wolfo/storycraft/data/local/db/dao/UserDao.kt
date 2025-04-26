package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wolfo.storycraft.data.local.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeUser(): Flow<UserEntity?>

    @Query("DELETE FROM user_profile")
    suspend fun clearUser()
}