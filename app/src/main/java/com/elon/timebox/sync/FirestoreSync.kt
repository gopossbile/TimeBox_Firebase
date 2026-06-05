package com.elon.timebox.sync

import com.elon.timebox.data.dao.*
import com.elon.timebox.data.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore 동기화 매니저
 *
 * Firestore 구조:
 * users/{userId}/
 *   ├── brain_dumps/{date}/items/{id}
 *   ├── mit_tasks/{date}/items/{id}
 *   ├── time_blocks/{date}/items/{id}
 *   └── evening_reviews/{date}
 *
 * 동기화 전략:
 * - 쓰기: Room 저장 → Firestore 업로드 (비동기)
 * - 읽기: Room 우선 (오프라인), 앱 시작 시 Firestore → Room 동기화
 */
@Singleton
class FirestoreSync @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val brainDumpDao: BrainDumpDao,
    private val mitTaskDao: MitTaskDao,
    private val timeBlockDao: TimeBlockDao,
    private val eveningReviewDao: EveningReviewDao
) {
    // Firestore 컬렉션 경로 헬퍼
    private fun userDoc(userId: String) = firestore.collection("users").document(userId)
    private fun dumpsCol(userId: String, date: String) =
        userDoc(userId).collection("brain_dumps").document(date).collection("items")
    private fun mitCol(userId: String, date: String) =
        userDoc(userId).collection("mit_tasks").document(date).collection("items")
    private fun blocksCol(userId: String, date: String) =
        userDoc(userId).collection("time_blocks").document(date).collection("items")
    private fun reviewDoc(userId: String, date: String) =
        userDoc(userId).collection("evening_reviews").document(date)

    // ──────────────────────────────────────────
    // 브레인 덤프 동기화
    // ──────────────────────────────────────────
    suspend fun uploadBrainDump(userId: String, entity: BrainDumpEntity) {
        try {
            dumpsCol(userId, entity.date)
                .document(entity.id.toString())
                .set(entity.toMap(), SetOptions.merge())
                .await()
        } catch (e: Exception) { /* 오프라인 시 무시 — Room에는 저장됨 */ }
    }

    suspend fun deleteBrainDump(userId: String, entity: BrainDumpEntity) {
        try {
            dumpsCol(userId, entity.date)
                .document(entity.id.toString())
                .delete().await()
        } catch (e: Exception) { }
    }

    suspend fun syncBrainDumps(userId: String, date: String) {
        try {
            val docs = dumpsCol(userId, date).get().await()
            docs.documents.forEach { doc ->
                val entity = BrainDumpEntity(
                    id = doc.getLong("id") ?: 0L,
                    content = doc.getString("content") ?: "",
                    date = doc.getString("date") ?: date,
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
                brainDumpDao.insert(entity)
            }
        } catch (e: Exception) { }
    }

    // ──────────────────────────────────────────
    // MIT 동기화
    // ──────────────────────────────────────────
    suspend fun uploadMitTask(userId: String, entity: MitTaskEntity) {
        try {
            mitCol(userId, entity.date)
                .document(entity.id.toString())
                .set(entity.toMap(), SetOptions.merge())
                .await()
        } catch (e: Exception) { }
    }

    suspend fun deleteMitTask(userId: String, entity: MitTaskEntity) {
        try {
            mitCol(userId, entity.date)
                .document(entity.id.toString())
                .delete().await()
        } catch (e: Exception) { }
    }

    suspend fun syncMitTasks(userId: String, date: String) {
        try {
            val docs = mitCol(userId, date).get().await()
            docs.documents.forEach { doc ->
                val entity = MitTaskEntity(
                    id = doc.getLong("id") ?: 0L,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    date = doc.getString("date") ?: date,
                    priority = doc.getLong("priority")?.toInt() ?: 1,
                    isCompleted = doc.getBoolean("isCompleted") ?: false,
                    completedAt = doc.getLong("completedAt"),
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
                mitTaskDao.insert(entity)
            }
        } catch (e: Exception) { }
    }

    // ──────────────────────────────────────────
    // 타임블록 동기화
    // ──────────────────────────────────────────
    suspend fun uploadTimeBlock(userId: String, entity: TimeBlockEntity) {
        try {
            blocksCol(userId, entity.date)
                .document(entity.id.toString())
                .set(entity.toMap(), SetOptions.merge())
                .await()
        } catch (e: Exception) { }
    }

    suspend fun deleteTimeBlock(userId: String, entity: TimeBlockEntity) {
        try {
            blocksCol(userId, entity.date)
                .document(entity.id.toString())
                .delete().await()
        } catch (e: Exception) { }
    }

    suspend fun syncTimeBlocks(userId: String, date: String) {
        try {
            val docs = blocksCol(userId, date).get().await()
            docs.documents.forEach { doc ->
                val entity = TimeBlockEntity(
                    id = doc.getLong("id") ?: 0L,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    date = doc.getString("date") ?: date,
                    startMinute = doc.getLong("startMinute")?.toInt() ?: 0,
                    durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 30,
                    category = doc.getString("category") ?: "WORK",
                    color = doc.getLong("color") ?: 0xFF6200EE,
                    isCompleted = doc.getBoolean("isCompleted") ?: false,
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
                timeBlockDao.insert(entity)
            }
        } catch (e: Exception) { }
    }

    // ──────────────────────────────────────────
    // 저녁 회고 동기화
    // ──────────────────────────────────────────
    suspend fun uploadEveningReview(userId: String, entity: EveningReviewEntity) {
        try {
            reviewDoc(userId, entity.date)
                .set(entity.toMap(), SetOptions.merge())
                .await()
        } catch (e: Exception) { }
    }

    suspend fun syncEveningReview(userId: String, date: String) {
        try {
            val doc = reviewDoc(userId, date).get().await()
            if (doc.exists()) {
                val entity = EveningReviewEntity(
                    id = doc.getLong("id") ?: 0L,
                    date = doc.getString("date") ?: date,
                    wins = doc.getString("wins") ?: "",
                    learnings = doc.getString("learnings") ?: "",
                    improvements = doc.getString("improvements") ?: "",
                    energyLevel = doc.getLong("energyLevel")?.toInt() ?: 3,
                    focusScore = doc.getLong("focusScore")?.toInt() ?: 3,
                    freeNote = doc.getString("freeNote") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0L,
                    updatedAt = doc.getLong("updatedAt") ?: 0L
                )
                eveningReviewDao.insert(entity)
            }
        } catch (e: Exception) { }
    }

    // ──────────────────────────────────────────
    // 오늘 날짜 전체 동기화 (앱 시작 시 호출)
    // ──────────────────────────────────────────
    suspend fun syncAll(userId: String, date: String) {
        syncBrainDumps(userId, date)
        syncMitTasks(userId, date)
        syncTimeBlocks(userId, date)
        syncEveningReview(userId, date)
    }
}

// ──────────────────────────────────────────
// Entity → Map 변환 확장함수 (Firestore 저장용)
// ──────────────────────────────────────────
fun BrainDumpEntity.toMap() = mapOf(
    "id" to id, "content" to content,
    "date" to date, "createdAt" to createdAt
)

fun MitTaskEntity.toMap() = mapOf(
    "id" to id, "title" to title, "description" to description,
    "date" to date, "priority" to priority, "isCompleted" to isCompleted,
    "completedAt" to completedAt, "createdAt" to createdAt
)

fun TimeBlockEntity.toMap() = mapOf(
    "id" to id, "title" to title, "description" to description,
    "date" to date, "startMinute" to startMinute, "durationMinutes" to durationMinutes,
    "category" to category, "color" to color,
    "isCompleted" to isCompleted, "createdAt" to createdAt
)

fun EveningReviewEntity.toMap() = mapOf(
    "id" to id, "date" to date, "wins" to wins,
    "learnings" to learnings, "improvements" to improvements,
    "energyLevel" to energyLevel, "focusScore" to focusScore,
    "freeNote" to freeNote, "createdAt" to createdAt, "updatedAt" to updatedAt
)
