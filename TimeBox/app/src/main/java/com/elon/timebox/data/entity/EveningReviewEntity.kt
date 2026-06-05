package com.elon.timebox.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 저녁 회고 Entity
 * 하루를 마무리하며 성과, 배움, 개선점을 기록
 * Elon Musk의 지속적 개선(Iteration) 철학에서 영감
 */
@Entity(tableName = "evening_reviews")
data class EveningReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,                      // "yyyy-MM-dd"

    // 오늘의 성과 (Win)
    val wins: String = "",

    // 배운 것
    val learnings: String = "",

    // 내일 개선할 것
    val improvements: String = "",

    // 에너지 레벨 (1~5점)
    val energyLevel: Int = 3,

    // 집중도 점수 (1~5점)
    val focusScore: Int = 3,

    // 자유 메모
    val freeNote: String = "",

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
