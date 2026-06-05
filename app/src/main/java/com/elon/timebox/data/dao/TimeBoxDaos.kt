package com.elon.timebox.data.dao

import androidx.room.*
import com.elon.timebox.data.entity.*
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────
// DAO = Data Access Object: DB 접근 인터페이스
// Flow = 코루틴 기반 실시간 데이터 스트림
// ─────────────────────────────────────────────

@Dao
interface BrainDumpDao {
    @Query("SELECT * FROM brain_dumps WHERE date = :date ORDER BY createdAt DESC")
    fun getByDate(date: String): Flow<List<BrainDumpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BrainDumpEntity): Long

    @Update
    suspend fun update(entity: BrainDumpEntity)

    @Delete
    suspend fun delete(entity: BrainDumpEntity)

    @Query("DELETE FROM brain_dumps WHERE date = :date")
    suspend fun deleteByDate(date: String)
}

@Dao
interface MitTaskDao {
    @Query("SELECT * FROM mit_tasks WHERE date = :date ORDER BY priority ASC")
    fun getByDate(date: String): Flow<List<MitTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MitTaskEntity): Long

    @Update
    suspend fun update(entity: MitTaskEntity)

    @Delete
    suspend fun delete(entity: MitTaskEntity)

    @Query("UPDATE mit_tasks SET isCompleted = :completed, completedAt = :completedAt WHERE id = :id")
    suspend fun updateCompletion(id: Long, completed: Boolean, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM mit_tasks WHERE date = :date")
    suspend fun countByDate(date: String): Int
}

@Dao
interface TimeBlockDao {
    @Query("SELECT * FROM time_blocks WHERE date = :date ORDER BY startMinute ASC")
    fun getByDate(date: String): Flow<List<TimeBlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TimeBlockEntity): Long

    @Update
    suspend fun update(entity: TimeBlockEntity)

    @Delete
    suspend fun delete(entity: TimeBlockEntity)

    @Query("UPDATE time_blocks SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletion(id: Long, completed: Boolean)

    // 특정 날짜의 겹치는 타임블록 조회 (충돌 방지)
    @Query("""
        SELECT * FROM time_blocks 
        WHERE date = :date 
        AND startMinute < :endMinute 
        AND (startMinute + durationMinutes) > :startMinute
        AND id != :excludeId
    """)
    suspend fun findOverlapping(date: String, startMinute: Int, endMinute: Int, excludeId: Long = -1): List<TimeBlockEntity>
}

@Dao
interface EveningReviewDao {
    @Query("SELECT * FROM evening_reviews WHERE date = :date LIMIT 1")
    fun getByDate(date: String): Flow<EveningReviewEntity?>

    @Query("SELECT * FROM evening_reviews ORDER BY date DESC")
    fun getAll(): Flow<List<EveningReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EveningReviewEntity): Long

    @Update
    suspend fun update(entity: EveningReviewEntity)

    @Query("DELETE FROM evening_reviews WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
