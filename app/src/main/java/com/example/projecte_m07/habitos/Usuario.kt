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