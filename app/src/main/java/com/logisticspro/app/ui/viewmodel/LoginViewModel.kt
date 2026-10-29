package com.logisticspro.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, sifre: String) {
        val validationError = validateCredentials(email, sifre)
        if (validationError != null) {
            _loginState.value = LoginState.Error(validationError)
        } else {
            // Simulate successful api call
            _loginState.value = LoginState.Success
        }
    }

    fun validateCredentials(email: String, sifre: String): String? {
        if (email.isBlank()) {
            return "Email veya kullanıcı adı boş olamaz"
        }
        // Basit demo testleri için kontrol kurallarını biraz ezdik, hem email formatına hem demo isme ("test.user") izin veriyoruz.
        if (!email.contains("@") && email != "test") {
            return "Geçerli bir email adresi veya kullanıcı adı giriniz"
        }
        if (sifre.length < 6) {
            return "Şifre en az 6 karakter olmalıdır"
        }
        
        // Mock veri: Sadece bu kullanıcı adı/email ve şifre kombinasyonunda başarılı olur
        if ((email == "test" && sifre == "123456") || (email == "surucu@logisticspro.com" && sifre == "123456")) {
            return null // Başarılı
        } else {
            return "Kullanıcı adı veya şifre hatalı!"
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

