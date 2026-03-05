package com.example.projecte_m07.habitos

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projecte_m07.R
import kotlinx.coroutines.*
import android.util.Log


class PruebaAPI : AppCompatActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prueba_api)

        container = findViewById(R.id.container)

        fetchAndShowHabitos()
    }

    private fun fetchAndShowHabitos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitos = HabitosAPI.API().getHabitos()

                withContext(Dispatchers.Main) {
                    container.removeAllViews() // limpiar container antes

                    if (habitos.isEmpty()) {
                        val tvEmpty = TextView(this@PruebaAPI)
                        tvEmpty.text = "No hay hábitos para mostrar."
                        container.addView(tvEmpty)
                    } else {
                        habitos.forEach { habito ->
                            val tv = TextView(this@PruebaAPI)
                            tv.text = "ID: ${habito.id}, Nombre: ${habito.nombre}, Hora: ${habito.hora}"
                            tv.textSize = 18f
                            container.addView(tv)
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("PruebaAPI", "Error al cargar hábitos", e)
                withContext(Dispatchers.Main) {
                    container.removeAllViews()
                    val tvError = TextView(this@PruebaAPI)
                    tvError.text = "Error al cargar hábitos: ${e.message}"
                    container.addView(tvError)
                }
            }
        }
    }
}
