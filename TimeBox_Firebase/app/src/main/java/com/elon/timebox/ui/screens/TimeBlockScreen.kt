package com.elon.timebox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elon.timebox.data.entity.TimeBlockEntity
import com.elon.timebox.viewmodel.TimeBlockViewModel

// 카테고리 목록
val CATEGORIES = listOf(
    Triple("WORK",     "업무",     Color(0xFF0096C7)),
    Triple("PERSONAL", "개인",     Color(0xFF7B2D8B)),
    Triple("HEALTH",   "건강",     Color(0xFF06D6A0)),
    Triple("LEARNING", "학습",     Color(0xFFFFB703)),
    Triple("OTHER",    "기타",     Color(0xFF6C757D))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeBlockScreen(viewModel: TimeBlockViewModel = hiltViewModel()) {
    val blocks by viewModel.blocks.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 헤더
        TimeBlockHeader(blockCount = blocks.size)

        // 타임라인 시각화 영역 (06:00 ~ 24:00)
        Text(
            "타임라인",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.primary
        )

        if (blocks.isEmpty()) {
            TimeBlockEmptyState()
        } else {
            // 블록 목록 (시간 순서 정렬됨)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(blocks, key = { it.id }) { block ->
                    TimeBlockRow(
                        block = block,
                        onToggle = { viewModel.toggleComplete(block) },
                        onDelete = { viewModel.deleteBlock(block) }
                    )
                }
            }
        }

        // 추가 버튼
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "타임블록 추가")
        }
    }

    // 에러
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("충돌 감지") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("확인") }
            }
        )
    }

    // 추가 다이얼로그
    if (showAddDialog) {
        TimeBlockAddDialog(
            onConfirm = { title, startH, startM, durationM, category, description ->
                val startMinute = startH * 60 + startM
                viewModel.addBlock(title, startMinute, durationM, category, description)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun TimeBlockHeader(blockCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B1B2F))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null,
                    tint = Color(0xFF00B4D8), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("타임블록", style = MaterialTheme.typography.headlineMedium,
                    color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("5분 단위 정밀 스케줄링 · $blockCount개 블록",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TimeBlockRow(
    block: TimeBlockEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val catColor = CATEGORIES.find { it.first == block.category }?.third ?: Color.Gray
    val blockColor = Color(block.color)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (!block.isCompleted) 1.dp else 0.dp,
                color = blockColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (block.isCompleted)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 카테고리 색상 바
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(64.dp)
                    .background(if (block.isCompleted) Color.Gray.copy(alpha = 0.4f) else blockColor)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // 시간 표시
            Column(
                modifier = Modifier.width(58.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(block.startTimeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
                Text("↕", color = MaterialTheme.colorScheme.outline)
                Text(block.endTimeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline)
            }

            Divider(modifier = Modifier.height(48.dp).width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    block.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (block.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(catColor.copy(alpha = if (block.isCompleted) 0.4f else 1f))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${CATEGORIES.find { it.first == block.category }?.second ?: block.category} · ${block.durationMinutes}분",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Checkbox(
                checked = block.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF06D6A0))
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun TimeBlockEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Icon(Icons.Default.Schedule, contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("하루를 5분 단위로 설계하세요",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        Text("Elon Musk는 5분 단위로 하루를 계획합니다",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeBlockAddDialog(
    onConfirm: (String, Int, Int, Int, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf("09") }
    var startMin by remember { mutableStateOf("00") }
    var duration by remember { mutableStateOf("30") } // 분
    var selectedCategory by remember { mutableStateOf("WORK") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("타임블록 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("블록 제목 *") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 시작 시간
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { if (it.length <= 2) startHour = it },
                        label = { Text("시작 시(0~23)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = startMin,
                        onValueChange = { if (it.length <= 2) startMin = it },
                        label = { Text("시작 분(0/5/10...)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // 지속 시간
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("지속 시간 (분, 5의 배수)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 카테고리
                Text("카테고리", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CATEGORIES.forEach { (key, label, color) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { selectedCategory = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.3f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("메모 (선택)") }, modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = startHour.toIntOrNull() ?: 9
                    val m = (startMin.toIntOrNull() ?: 0).let { (it / 5) * 5 } // 5분 단위 정규화
                    val d = (duration.toIntOrNull() ?: 30).let { maxOf(5, (it / 5) * 5) }
                    if (title.isNotBlank()) onConfirm(title, h, m, d, selectedCategory, description)
                },
                enabled = title.isNotBlank()
            ) { Text("추가") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}
