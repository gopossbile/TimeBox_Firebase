package com.elon.timebox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elon.timebox.data.entity.EveningReviewEntity
import com.elon.timebox.ui.theme.NeonGreen
import com.elon.timebox.ui.theme.WarmAmber
import com.elon.timebox.viewmodel.EveningReviewViewModel

@Composable
fun EveningReviewScreen(viewModel: EveningReviewViewModel = hiltViewModel()) {
    val review by viewModel.review.collectAsState()
    val scrollState = rememberScrollState()

    // 폼 상태 — 기존 저장값으로 초기화
    var wins by remember(review) { mutableStateOf(review?.wins ?: "") }
    var learnings by remember(review) { mutableStateOf(review?.learnings ?: "") }
    var improvements by remember(review) { mutableStateOf(review?.improvements ?: "") }
    var energyLevel by remember(review) { mutableStateOf(review?.energyLevel ?: 3) }
    var focusScore by remember(review) { mutableStateOf(review?.focusScore ?: 3) }
    var freeNote by remember(review) { mutableStateOf(review?.freeNote ?: "") }

    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // 헤더
        EveningReviewHeader(hasReview = review != null)

        Spacer(modifier = Modifier.height(8.dp))

        // ── 오늘의 성과 ──
        ReviewSection(
            icon = Icons.Default.EmojiEvents,
            iconColor = WarmAmber,
            title = "오늘의 성과 (Win)",
            hint = "오늘 잘한 일, 완료한 것, 작은 승리들을 적어보세요..."
        ) {
            OutlinedTextField(
                value = wins,
                onValueChange = { wins = it; isSaved = false },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("예: MIT 3개 모두 완료, 집중력 유지 3시간...") },
                maxLines = 5,
                colors = reviewTextFieldColors()
            )
        }

        // ── 배운 것 ──
        ReviewSection(
            icon = Icons.Default.School,
            iconColor = Color(0xFF00B4D8),
            title = "오늘 배운 것",
            hint = "새로 알게 된 사실, 깨달음, 인사이트를 기록하세요"
        ) {
            OutlinedTextField(
                value = learnings,
                onValueChange = { learnings = it; isSaved = false },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("예: 코루틴 Flow는 Cold Stream이다...") },
                maxLines = 5,
                colors = reviewTextFieldColors()
            )
        }

        // ── 내일 개선할 것 ──
        ReviewSection(
            icon = Icons.Default.TrendingUp,
            iconColor = NeonGreen,
            title = "내일 개선할 것",
            hint = "더 잘할 수 있었던 것, 내일의 나에게 조언을 남기세요"
        ) {
            OutlinedTextField(
                value = improvements,
                onValueChange = { improvements = it; isSaved = false },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("예: SNS 차단 시간을 오전 중으로 앞당기기...") },
                maxLines = 5,
                colors = reviewTextFieldColors()
            )
        }

        // ── 점수 평가 ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "오늘 하루 평가",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 에너지 레벨
                ScoreSlider(
                    label = "⚡ 에너지 레벨",
                    value = energyLevel,
                    onValueChange = { energyLevel = it; isSaved = false },
                    lowLabel = "방전",
                    highLabel = "넘침"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 집중도
                ScoreSlider(
                    label = "🎯 집중도",
                    value = focusScore,
                    onValueChange = { focusScore = it; isSaved = false },
                    lowLabel = "산만",
                    highLabel = "몰입"
                )
            }
        }

        // ── 자유 메모 ──
        ReviewSection(
            icon = Icons.Default.Notes,
            iconColor = Color(0xFF9C6ADE),
            title = "자유 메모",
            hint = "내일 아침에 보고 싶은 것, 감사한 것 등 자유롭게"
        ) {
            OutlinedTextField(
                value = freeNote,
                onValueChange = { freeNote = it; isSaved = false },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("내일의 나에게 전하는 말...") },
                maxLines = 6,
                colors = reviewTextFieldColors()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── 저장 버튼 ──
        Button(
            onClick = {
                viewModel.saveReview(
                    wins = wins,
                    learnings = learnings,
                    improvements = improvements,
                    energyLevel = energyLevel,
                    focusScore = focusScore,
                    freeNote = freeNote
                )
                isSaved = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSaved) NeonGreen else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                if (isSaved) Icons.Default.CheckCircle else Icons.Default.Save,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isSaved) "저장 완료!" else "회고 저장",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EveningReviewHeader(hasReview: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A0533), Color(0xFF2D1B4E))
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.NightsStay,
                    contentDescription = null,
                    tint = Color(0xFFCE93D8),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "저녁 회고",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (hasReview) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            "  작성됨  ",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "오늘을 돌아보고 내일을 더 잘 살아가세요",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ReviewSection(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    hint: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )
            content()
        }
    }
}

@Composable
private fun ScoreSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    lowLabel: String,
    highLabel: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            // 별점 표시
            Row {
                repeat(5) { i ->
                    Icon(
                        imageVector = if (i < value) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = WarmAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(lowLabel, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            Text(highLabel, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun reviewTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
)
