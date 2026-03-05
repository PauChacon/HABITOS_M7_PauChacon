package com.example.recyclerview.habitos

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date
// Clase para representar un hábito completo
@Parcelize
data class Habito(
    @SerializedName("id")
    val id: Int,
    val nombre: String,
    val categoria: String,
    val importante: Boolean,
    val hora: Date?
) : Parcelable

// Clase para crear un nuevo hábito
@Parcelize
data class HabitoCreate(
    val nombre: String,
    val categoria: String,
    val importante: Boolean,
    val hora: String
) : Parcelable

// Lista de hábitos de prueba para mostrar en la app
class HabitosProvider {
    companion object {
        val habitos: List<Habito> = listOf(
            Habito(1, "Leer 30 minutos", "Ocio", true, "08:00".toHourMinuteDate("HH:mm")),
            Habito(2, "Hacer ejercicio", "Salud", true, "07:30".toHourMinuteDate("HH:mm")),
            Habito(3, "Meditar", "Salud", false, "22:00".toHourMinuteDate("HH:mm")),
            Habito(4, "Revisar correos", "Productividad", false, "09:00".toHourMinuteDate("HH:mm"))
        )
    }
}