package com.example.projecte_m07.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun validate(username: String, email: String, phone: String, password: String, confirmPassword: String): Boolean {
        _error.value = null

        if (username.length <= 3) {
            _error.value = "El nom d'usuari ha de tenir més de 3 caràcters"
            return false
        }

        if (!phone.matches(Regex("^\\d{9}$"))) {
            _error.value = "El telèfon ha de tenir 9 xifres"
            return false
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
            _error.value = "El correu electrònic no és vàlid"
            return false
        }

        if (!isStrongPassword(password)) {
            _error.value = "Contrasenya insegura"
            return false
        }

        if (password != confirmPassword) {
            _error.value = "Les contrasenyes no coincideixen"
            return false
        }
        if (username.length > 25) {
            _error.value = "El nom d'usuari ha de tenir menys de 25 caràcters"
            return false
        }


        return true
    }

    private fun isStrongPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")
        return regex.matches(password)
    }
}
