package com.elon.timebox.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.elon.timebox.data.entity.MitTaskEntity
import com.elon.timebox.ui.theme.NeonGreen
import com.elon.timebox.ui.theme.WarmAmber
import com.elon.timebox.viewmodel.MitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitScreen(viewModel: MitViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val completedCount = tasks.count { it.isCompleted }
    val progress = if (tasks.isEmpty()) 0f else completedCount.toFloat() / tasks.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 헤더 + 진행률
        MitHeader(
            completedCount = completedCount,
            totalCount = tasks.size,
            progress = progress
        )

        // 과제 목록
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                MitTaskCard(
                    task = task,
                    onToggle = { viewModel.toggleComplete(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }

            // 빈 슬롯 표시 (최대 3개)
            val remainingSlots = 3 - tasks.size
            items(remainingSlots) { index ->
                MitEmptySlot(slotNumber = tasks.size + index + 1)
            }
        }

        // 추가 버튼
        if (tasks.size < 3) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("핵심 과제 추가 (${tasks.size}/3)")
            }
        }
    }

    // 에러 스낵바
    errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            // Snackbar는 Scaffold에서 처리하는 것이 이상적이나 여기선 Dialog로 대체
            viewModel.clearError()
        }
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("알림") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("확인") }
            }
        )
    }

    // 추가 다이얼로그
    if (showAddDialog) {
        MitAddDialog(
            onConfirm = { title, description ->
                viewModel.addTask(title, description)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun MitHeader(completedCount: Int, totalCount: Int, progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A237E), Color(0xFF283593))
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null,
                    tint = WarmAmber, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("MIT · 핵심 과제 3가지",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("오늘 반드시 완료해야 할 최우선 과제",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))

            // 진행률 바
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = NeonGreen,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("$completedCount / $totalCount 완료",
                color = NeonGreen, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun MitTaskCard(
    task: MitTaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColors = listOf(
        Color(0xFFEF233C), // 1순위: 빨강
        Color(0xFFFF9F1C), // 2순위: 주황
        Color(0xFF06D6A0)  // 3순위: 초록
    )
    val priorityColor = priorityColors.getOrElse(task.priority - 1) { priorityColors.last() }

    val bgColor by animateColorAsState(
        if (task.isCompleted)
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        else MaterialTheme.colorScheme.surface,
        label = "cardColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, if (!task.isCompleted) priorityColor.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 우선순위 뱃지
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) Color.Gray.copy(alpha = 0.3f) else priorityColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${task.priority}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = NeonGreen,
                    uncheckedColor = priorityColor
                )
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun MitEmptySlot(slotNumber: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("$slotNumber", color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("빈 슬롯 — 핵심 과제를 추가하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
        }
    }
}

@Composable
private fun MitAddDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("핵심 과제 추가") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("과제 제목 *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("부가 설명 (선택)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, description) },
                enabled = title.isNotBlank()
            ) { Text("추가") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}
