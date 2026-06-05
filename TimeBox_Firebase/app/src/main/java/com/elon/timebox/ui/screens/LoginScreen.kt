package com.elon.timebox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elon.timebox.ui.theme.ElectricBlue
import com.elon.timebox.ui.theme.NeonGreen
import com.elon.timebox.viewmodel.AuthState
import com.elon.timebox.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // 로그인 성공 시 메인으로 이동
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050A14), Color(0xFF0D1B2A), Color(0xFF050A14))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // 로고
            Text("⚡", fontSize = 72.sp)

            Text(
                "TimeBox",
                style = MaterialTheme.typography.displayLarge,
                color = ElectricBlue,
                fontWeight = FontWeight.Bold
            )

            Text(
                "일론 머스크 스타일\n5분 단위 타임박싱 플래너",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 기능 소개
            listOf(
                "🧠 브레인 덤프 — 생각을 즉시 비우기",
                "⭐ MIT — 하루 핵심 과제 3가지",
                "⏱ 타임블록 — 5분 단위 정밀 스케줄",
                "🌙 저녁 회고 — 매일 성장 기록"
            ).forEach { feature ->
                Text(
                    feature,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google 로그인 버튼
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(color = ElectricBlue)
                    Text("로그인 중...", color = Color.White.copy(alpha = 0.6f))
                }
                is AuthState.Error -> {
                    Text(
                        (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    GoogleSignInButton { viewModel.signInWithGoogle(context) }
                }
                else -> {
                    GoogleSignInButton { viewModel.signInWithGoogle(context) }
                }
            }

            Text(
                "로그인하면 모든 기기에서\n데이터가 자동 동기화됩니다",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color(0xFF4285F4),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "Google로 로그인",
            color = Color(0xFF202124),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
