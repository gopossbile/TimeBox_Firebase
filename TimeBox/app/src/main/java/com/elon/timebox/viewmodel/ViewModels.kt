package com.elon.timebox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elon.timebox.data.entity.*
import com.elon.timebox.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// ───────────────────────────────────────
// ViewModel: UI와 Repository 사이 중간 계층
// StateFlow = Compose UI가 구독하는 상태 스트림
// viewModelScope = ViewModel 생명주기에 맞는 코루틴 스코프
// ───────────────────────────────────────

val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
fun today(): String = LocalDate.now().format(DATE_FORMATTER)

// ──────────────────────────────────────────────
// 브레인 덤프 ViewModel
// ──────────────────────────────────────────────
@HiltViewModel
class BrainDumpViewModel @Inject constructor(
    private val repository: BrainDumpRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(today())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val dumps: StateFlow<List<BrainDumpEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addDump(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.save(
                BrainDumpEntity(
                    content = content.trim(),
                    date = _selectedDate.value
                )
            )
        }
    }

    fun updateDump(entity: BrainDumpEntity, newContent: String) {
        viewModelScope.launch {
            repository.update(entity.copy(content = newContent.trim()))
        }
    }

    fun deleteDump(entity: BrainDumpEntity) {
        viewModelScope.launch { repository.delete(entity) }
    }

    fun selectDate(date: String) { _selectedDate.value = date }
}

// ──────────────────────────────────────────────
// MIT (핵심 과제 3가지) ViewModel
// ──────────────────────────────────────────────
@HiltViewModel
class MitViewModel @Inject constructor(
    private val repository: MitTaskRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(today())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val tasks: StateFlow<List<MitTaskEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, description: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            val canAdd = repository.canAddMore(_selectedDate.value)
            if (!canAdd) {
                _errorMessage.value = "MIT는 하루에 최대 3개까지만 등록할 수 있습니다"
                return@launch
            }
            val currentCount = tasks.value.size
            repository.save(
                MitTaskEntity(
                    title = title.trim(),
                    description = description.trim(),
                    date = _selectedDate.value,
                    priority = currentCount + 1
                )
            )
        }
    }

    fun toggleComplete(task: MitTaskEntity) {
        viewModelScope.launch {
            repository.toggleComplete(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: MitTaskEntity) {
        viewModelScope.launch { repository.delete(task) }
    }

    fun clearError() { _errorMessage.value = null }

    fun selectDate(date: String) { _selectedDate.value = date }
}

// ──────────────────────────────────────────────
// 타임블록 ViewModel
// ──────────────────────────────────────────────
@HiltViewModel
class TimeBlockViewModel @Inject constructor(
    private val repository: TimeBlockRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(today())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val blocks: StateFlow<List<TimeBlockEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBlock(
        title: String,
        startMinute: Int,
        durationMinutes: Int,
        category: String = "WORK",
        description: String = ""
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val categoryColor = getCategoryColor(category)
            val result = repository.save(
                TimeBlockEntity(
                    title = title.trim(),
                    description = description.trim(),
                    date = _selectedDate.value,
                    startMinute = startMinute,
                    durationMinutes = durationMinutes,
                    category = category,
                    color = categoryColor
                )
            )
            result.onFailure { _errorMessage.value = it.message }
        }
    }

    fun toggleComplete(block: TimeBlockEntity) {
        viewModelScope.launch {
            repository.toggleComplete(block.id, !block.isCompleted)
        }
    }

    fun deleteBlock(block: TimeBlockEntity) {
        viewModelScope.launch { repository.delete(block) }
    }

    fun clearError() { _errorMessage.value = null }

    fun selectDate(date: String) { _selectedDate.value = date }

    private fun getCategoryColor(category: String): Long = when (category) {
        "WORK"     -> 0xFF0096C7
        "PERSONAL" -> 0xFF7B2D8B
        "HEALTH"   -> 0xFF06D6A0
        "LEARNING" -> 0xFFFFB703
        else       -> 0xFF6C757D
    }
}

// ──────────────────────────────────────────────
// 저녁 회고 ViewModel
// ──────────────────────────────────────────────
@HiltViewModel
class EveningReviewViewModel @Inject constructor(
    private val repository: EveningReviewRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(today())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val review: StateFlow<EveningReviewEntity?> = _selectedDate
        .flatMapLatest { date -> repository.getByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allReviews: StateFlow<List<EveningReviewEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveReview(
        wins: String,
        learnings: String,
        improvements: String,
        energyLevel: Int,
        focusScore: Int,
        freeNote: String
    ) {
        viewModelScope.launch {
            val existing = review.value
            val entity = if (existing != null) {
                existing.copy(
                    wins = wins,
                    learnings = learnings,
                    improvements = improvements,
                    energyLevel = energyLevel,
                    focusScore = focusScore,
                    freeNote = freeNote,
                    updatedAt = System.currentTimeMillis()
                )
            } else {
                EveningReviewEntity(
                    date = _selectedDate.value,
                    wins = wins,
                    learnings = learnings,
                    improvements = improvements,
                    energyLevel = energyLevel,
                    focusScore = focusScore,
                    freeNote = freeNote
                )
            }
            if (existing != null) repository.update(entity)
            else repository.save(entity)
        }
    }

    fun selectDate(date: String) { _selectedDate.value = date }
}
