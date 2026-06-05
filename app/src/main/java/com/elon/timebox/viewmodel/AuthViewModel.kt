package com.elon.timebox.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elon.timebox.auth.AuthManager
import com.elon.timebox.sync.FirestoreSync
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()
    data class LoggedIn(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val firestoreSync: FirestoreSync
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                _authState.value = if (user != null) {
                    AuthState.LoggedIn(user)
                } else {
                    AuthState.LoggedOut
                }
            }
        }
    }

    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authManager.signInWithGoogle(activityContext)
                .onSuccess { user ->
                    // 로그인 성공 시 오늘 데이터 동기화
                    firestoreSync.syncAll(user.uid, today())
                    _authState.value = AuthState.LoggedIn(user)
                }
                .onFailure { e ->
                    _authState.value = AuthState.Error(e.message ?: "로그인 실패")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
            _authState.value = AuthState.LoggedOut
        }
    }
}
