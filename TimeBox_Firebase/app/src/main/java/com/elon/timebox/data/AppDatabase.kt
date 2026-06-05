package com.elon.timebox.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elon.timebox.data.dao.*
import com.elon.timebox.data.entity.*

/**
 * Room Database 싱글톤
 * @Database 어노테이션으로 테이블(Entity) 등록
 * version: 스키마 변경 시 증가, exportSchema: 마이그레이션 기록 여부
 */
@Database(
    entities = [
        BrainDumpEntity::class,
        MitTaskEntity::class,
        TimeBlockEntity::class,
        EveningReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brainDumpDao(): BrainDumpDao
    abstract fun mitTaskDao(): MitTaskDao
    abstract fun timeBlockDao(): TimeBlockDao
    abstract fun eveningReviewDao(): EveningReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timebox_database"
                )
                    .fallbackToDestructiveMigration() // 개발 중 스키마 변경 대응
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
