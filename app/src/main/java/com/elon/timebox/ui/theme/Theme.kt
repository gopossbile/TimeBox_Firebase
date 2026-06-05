package com.elon.timebox.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ──────────────────────────────────────────────────────────
// Elon Musk 스타일: 미니멀하고 기능 중심적인 색상 팔레트
// 다크모드: 딥 블랙 + 일렉트릭 블루 (Tesla 대시보드 연상)
// 라이트모드: 클린 화이트 + 딥 네이비
// ──────────────────────────────────────────────────────────

// 브랜드 컬러
val ElectricBlue = Color(0xFF00B4D8)
val DeepNavy = Color(0xFF03045E)
val NeonGreen = Color(0xFF06D6A0)
val WarmAmber = Color(0xFFFFB703)
val CriticalRed = Color(0xFFEF233C)

// 카테고리 색상
val CategoryWork = Color(0xFF0096C7)
val CategoryPersonal = Color(0xFF7B2D8B)
val CategoryHealth = Color(0xFF06D6A0)
val CategoryLearning = Color(0xFFFFB703)
val CategoryOther = Color(0xFF6C757D)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003554),
    onPrimaryContainer = ElectricBlue,
    secondary = NeonGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D3A),
    onSecondaryContainer = NeonGreen,
    tertiary = WarmAmber,
    background = Color(0xFF050A14),        // 딥 스페이스 블랙
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF0D1B2A),           // 카드 배경
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1A2744),
    error = CriticalRed,
    outline = Color(0xFF2A3F5F)
)

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E5FF),
    onPrimaryContainer = DeepNavy,
    secondary = Color(0xFF006494),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCEBFF),
    onSecondaryContainer = Color(0xFF001E2E),
    tertiary = Color(0xFFA06C00),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF1A1A2E),
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFEDF2F4),
    error = CriticalRed,
    outline = Color(0xFFBBC8D4)
)

@Composable
fun TimeBoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic Color: Android 12+ 기기에서 배경화면 색상 추출 기능
    dynamicColor: Boolean = false, // Elon 스타일 고정 팔레트를 위해 비활성화
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TimeBoxTypography,
        content = content
    )
}
