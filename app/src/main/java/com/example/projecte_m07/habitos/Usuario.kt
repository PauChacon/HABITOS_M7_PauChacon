package com.example.projecte_m07.habitos

data class UsuarioCreate(
    val username: String,
    val email: String,
    val telefono: String,
    val password: String
)

data class UsuarioLogin(
    val username: String,
    val password: String
)

data class UsuarioResponse(
    val id: Int,
    val username: String,
    val email: String,
    val telefono: String
)

data class UsuarioUpdateRequest(
    val username: String,
    val email: String,
    val telefono: String
)

data class HistorialCreate(
    val tipo: String,
    val nombre_habito: String,
    val categoria: String,
    val importante: Boolean,
    val hora: String
)

data class HistorialEntry(
    val id: Int,
    val tipo: String,
    val nombre_habito: String,
    val categoria: String,
    val importante: Boolean,
    val hora: String,
    val fecha: String
)