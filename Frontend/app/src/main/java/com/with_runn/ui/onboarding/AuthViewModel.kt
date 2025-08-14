package com.with_runn.ui.onboarding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.with_runn.data.TokenManager
import com.with_runn.login.AuthRepository
import com.with_runn.login.LoginRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginRes?>(null)
    val loginState: StateFlow<LoginRes?> = _loginState

    fun loginWithGoogle(email: String, id: String, idToken: String?) {
        viewModelScope.launch {
            val res = authRepo.login(email, id, idToken)
            if (res != null) {
                // 세션 저장
                TokenManager.setAccessToken(res.accessToken)
                TokenManager.setUserId(res.memberId)
            }
            _loginState.value = res
        }
    }
}
