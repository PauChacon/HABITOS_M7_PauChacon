package com.example.projecte_m07.viewmodel

import androidx.lifecycle.ViewModel

class EditarPerfilViewModel : ViewModel() {

    fun validate(username: String, email: String, telefono: String): Boolean {

        if (username.length <= 3) return false

        if (username.length >= 26) return false

        if (!telefono.matches(Regex("^\\d{9}$"))) return false

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) return false

        return true
    }
}