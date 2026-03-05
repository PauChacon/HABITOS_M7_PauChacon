package com.example.projecte_m07

enum class FilterType(val displayName: String) {
    NONE("Sin filtro"),
    BY_TIME("Por hora (temprano → tarde)"),
    BY_IMPORTANCE("Por importancia (importantes primero)")
}