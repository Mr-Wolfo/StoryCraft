package com.wolfo.storycraft.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wolfo.storycraft.data.local.db.dao.StoryDao
import com.wolfo.storycraft.data.local.db.dao.UserDao
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.UserEntity

@Database(
    entities = [
        StoryEntity::class,
        PageEntity::class,
        ChoiceEntity::class,
        UserEntity::class
    ],
    version = 2
)
@TypeConverters(StoryEntity.Converters::class)
abstract class AppDb: RoomDatabase() {
    abstract val storyDao: StoryDao
    abstract val userDao: UserDao
    companion object{
        fun createDataBase(context: Context): AppDb{
            return Room.databaseBuilder(context,
                AppDb::class.java,
                "story_craft.db").build()
        }
    }
}