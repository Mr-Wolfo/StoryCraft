package com.wolfo.storycraft.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity

@Dao
interface ChoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChoices(choices: List<ChoiceEntity>)
}