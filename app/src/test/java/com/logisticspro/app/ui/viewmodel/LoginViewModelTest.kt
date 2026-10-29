package com.logisticspro.app.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        // ViewModel'i test öncesi hazırlıyoruz
        viewModel = LoginViewModel()
    }

    @Test
    fun `Email bos girildiginde hata vermeli`() {
        // Given
        val email = ""
        val sifre = "123456"

        // When
        val result = viewModel.validateCredentials(email, sifre)

        // Then
        assertEquals("Email veya kullanıcı adı boş olamaz", result)
    }

    @Test
    fun `Email degeri '@' icermiyorsa hata vermeli`() {
        // Given
        val email = "gecersizemail.com"
        val sifre = "123456"

        // When
        val result = viewModel.validateCredentials(email, sifre)

        // Then
        assertEquals("Geçerli bir email adresi veya kullanıcı adı giriniz", result)
    }

    @Test
    fun `Sifre 6 karakterden kisa girildiginde hata vermeli`() {
        // Given
        val email = "surucu@logisticspro.com"
        val sifre = "12345"

        // When
        val result = viewModel.validateCredentials(email, sifre)

        // Then
        assertEquals("Şifre en az 6 karakter olmalıdır", result)
    }

    @Test
    fun `Dogru girildiginde basarili donmeli (null donmeli)`() {
        // Given
        val email = "surucu@logisticspro.com"
        val sifre = "123456"

        // When
        val result = viewModel.validateCredentials(email, sifre)

        // Then
        assertNull(result)
    }
}

