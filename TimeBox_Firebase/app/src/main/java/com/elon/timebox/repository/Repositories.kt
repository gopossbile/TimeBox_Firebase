package com.elon.timebox.repository

import com.elon.timebox.data.dao.*
import com.elon.timebox.data.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────
// Repository 패턴:
// ViewModel ↔ Repository ↔ DAO ↔ Room DB
// UI 로직과 데이터 로직을 분리하는 계층
// ─────────────────────────────────────────────────────────

@Singleton
class BrainDumpRepository @Inject constructor(
    private val dao: BrainDumpDao
) {
    fun getByDate(date: String): Flow<List<BrainDumpEntity>> = dao.getByDate(date)

    suspend fun save(entity: BrainDumpEntity): Long = dao.insert(entity)

    suspend fun update(entity: BrainDumpEntity) = dao.update(entity)

    suspend fun delete(entity: BrainDumpEntity) = dao.delete(entity)
}

@Singleton
class MitTaskRepository @Inject constructor(
    private val dao: MitTaskDao
) {
    fun getByDate(date: String): Flow<List<MitTaskEntity>> = dao.getByDate(date)

    suspend fun save(entity: MitTaskEntity): Long = dao.insert(entity)

    suspend fun update(entity: MitTaskEntity) = dao.update(entity)

    suspend fun delete(entity: MitTaskEntity) = dao.delete(entity)

    suspend fun toggleComplete(id: Long, completed: Boolean) {
        dao.updateCompletion(
            id = id,
            completed = completed,
            completedAt = if (completed) System.currentTimeMillis() else null
        )
    }

    // MIT는 날짜당 최대 3개 제한
    suspend fun canAddMore(date: String): Boolean = dao.countByDate(date) < 3
}

@Singleton
class TimeBlockRepository @Inject constructor(
    private val dao: TimeBlockDao
) {
    fun getByDate(date: String): Flow<List<TimeBlockEntity>> = dao.getByDate(date)

    suspend fun save(entity: TimeBlockEntity): Result<Long> {
        // 5분 단위 검증
        if (entity.startMinute % 5 != 0) {
            return Result.failure(IllegalArgumentException("시작 시간은 5분 단위여야 합니다"))
        }
        if (entity.durationMinutes % 5 != 0 || entity.durationMinutes < 5) {
            return Result.failure(IllegalArgumentException("지속 시간은 5분 이상, 5분 단위여야 합니다"))
        }

        // 충돌 검사
        val overlapping = dao.findOverlapping(
            date = entity.date,
            startMinute = entity.startMinute,
            endMinute = entity.endMinute,
            excludeId = entity.id
        )
        if (overlapping.isNotEmpty()) {
            return Result.failure(IllegalStateException("해당 시간대에 이미 등록된 블록이 있습니다"))
        }

        return Result.success(dao.insert(entity))
    }

    suspend fun update(entity: TimeBlockEntity) = dao.update(entity)

    suspend fun delete(entity: TimeBlockEntity) = dao.delete(entity)

    suspend fun toggleComplete(id: Long, completed: Boolean) {
        dao.updateCompletion(id, completed)
    }
}

@Singleton
class EveningReviewRepository @Inject constructor(
    private val dao: EveningReviewDao
) {
    fun getByDate(date: String): Flow<EveningReviewEntity?> = dao.getByDate(date)

    fun getAll(): Flow<List<EveningReviewEntity>> = dao.getAll()

    suspend fun save(entity: EveningReviewEntity): Long = dao.insert(entity)

    suspend fun update(entity: EveningReviewEntity) = dao.update(entity)
}
