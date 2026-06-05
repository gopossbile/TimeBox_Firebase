package com.elon.timebox.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 브레인 덤프 Entity
 * 머릿속에 떠오르는 모든 생각을 빠르게 적어두는 공간
 * Elon Musk의 First Principles Thinking에서 영감을 받음
 */
@Entity(tableName = "brain_dumps")
data class BrainDumpEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,           // 브레인 덤프 내용
    val date: String,              // 날짜 "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
)
