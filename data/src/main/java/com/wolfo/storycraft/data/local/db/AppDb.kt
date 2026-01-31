package com.wolfo.storycraft.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import com.wolfo.storycraft.data.local.db.dao.ChoiceDao
import com.wolfo.storycraft.data.local.db.dao.PageDao
import com.wolfo.storycraft.data.local.db.dao.ReviewDao
import com.wolfo.storycraft.data.local.db.dao.StoryDao
import com.wolfo.storycraft.data.local.db.dao.StoryDraftDao
import com.wolfo.storycraft.data.local.db.dao.StoryTagCrossRefDao
import com.wolfo.storycraft.data.local.db.dao.TagDao
import com.wolfo.storycraft.data.local.db.dao.UserDao
import com.wolfo.storycraft.data.local.db.entity.ChoiceDraftEntity
import com.wolfo.storycraft.data.local.db.entity.ChoiceEntity
import com.wolfo.storycraft.data.local.db.entity.PageDraftEntity
import com.wolfo.storycraft.data.local.db.entity.PageEntity
import com.wolfo.storycraft.data.local.db.entity.ReviewEntity
import com.wolfo.storycraft.data.local.db.entity.StoryDraftEntity
import com.wolfo.storycraft.data.local.db.entity.StoryEntity
import com.wolfo.storycraft.data.local.db.entity.StoryTagCrossRef
import com.wolfo.storycraft.data.local.db.entity.TagEntity
import com.wolfo.storycraft.data.local.db.entity.UriConverter
import com.wolfo.storycraft.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        StoryEntity::class,
        PageEntity::class,
        ChoiceEntity::class,
        ReviewEntity::class,
        TagEntity::class,
        StoryTagCrossRef::class,
        ChoiceDraftEntity::class,
        PageDraftEntity::class,

        StoryDraftEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class, ListConverter::class, AuthorConverter::class, UriConverter::class)
abstract class StoryAppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun storyDao(): StoryDao
    abstract fun pageDao(): PageDao
    abstract fun choiceDao(): ChoiceDao
    abstract fun reviewDao(): ReviewDao
    abstract fun tagDao(): TagDao
    abstract fun storyTagCrossRefDao(): StoryTagCrossRefDao
    abstract fun storyDraftDao(): StoryDraftDao

    suspend fun <R> runInTransaction(block: suspend () -> R): R {
        return withTransaction { block() }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryAppDatabase? = null
        private const val DATABASE_NAME = "storyapp_database"

        fun getInstance(context: Context): StoryAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StoryAppDatabase::class.java,
                    DATABASE_NAME
                )
                    // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}