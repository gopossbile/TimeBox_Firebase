package com.elon.timebox.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TimeBlock Entity
 * 5분 단위로 하루를 구획화하는 타임박싱 핵심 기능
 * Elon Musk는 5분 단위로 하루 일정을 관리하는 것으로 알려져 있음
 *
 * category: WORK(업무), PERSONAL(개인), HEALTH(건강), LEARNING(학습), OTHER(기타)
 */
@Entity(tableName = "time_blocks")
data class TimeBlockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String,              // "yyyy-MM-dd"
    val startMinute: Int,          // 하루 시작부터 분 단위 (예: 9시 = 540분)
    val durationMinutes: Int,      // 5의 배수 (최소 5분)
    val category: String = "WORK",
    val color: Long = 0xFF6200EE,  // 카테고리별 색상 (ARGB Long)
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // 종료 분 계산 (computed property)
    val endMinute: Int get() = startMinute + durationMinutes

    // 시간 표시용 문자열 (예: "09:00")
    val startTimeLabel: String get() {
        val h = startMinute / 60
        val m = startMinute % 60
        return "%02d:%02d".format(h, m)
    }

    val endTimeLabel: String get() {
        val h = endMinute / 60
        val m = endMinute % 60
        return "%02d:%02d".format(h, m)
    }
}
