package com.elon.timebox.di

import android.content.Context
import androidx.room.Room
import com.elon.timebox.data.AppDatabase
import com.elon.timebox.data.dao.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "timebox_database")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides fun provideBrainDumpDao(db: AppDatabase): BrainDumpDao = db.brainDumpDao()
    @Provides fun provideMitTaskDao(db: AppDatabase): MitTaskDao = db.mitTaskDao()
    @Provides fun provideTimeBlockDao(db: AppDatabase): TimeBlockDao = db.timeBlockDao()
    @Provides fun provideEveningReviewDao(db: AppDatabase): EveningReviewDao = db.eveningReviewDao()

    // Firebase 싱글톤 제공
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
