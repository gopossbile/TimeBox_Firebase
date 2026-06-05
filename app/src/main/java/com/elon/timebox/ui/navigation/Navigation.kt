package com.elon.timebox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object BrainDump : Screen("brain_dump", "브레인덤프", Icons.Default.Psychology)
    object Mit : Screen("mit", "핵심 3가지", Icons.Default.Star)
    object TimeBlock : Screen("time_block", "타임블록", Icons.Default.AccessTime)
    object EveningReview : Screen("evening_review", "저녁 회고", Icons.Default.NightsStay)

    companion object {
        val bottomNavItems = listOf(BrainDump, Mit, TimeBlock, EveningReview)
    }
}
