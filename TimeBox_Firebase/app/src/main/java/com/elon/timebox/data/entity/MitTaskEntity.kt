package com.elon.timebox.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * MIT (Most Important Tasks) Entity
 * 하루에 반드시 완료해야 할 핵심 3가지 과제
 * Elon Musk의 우선순위 집중 방식에서 착안
 */
@Entity(tableName = "mit_tasks")
data class MitTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,             // 과제 제목
    val description: String = "", // 부가 설명
    val date: String,              // 날짜 "yyyy-MM-dd"
    val priority: Int,             // 1, 2, 3 순서 (1이 가장 중요)
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
