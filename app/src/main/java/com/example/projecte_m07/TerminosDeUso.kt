package com.example.projecte_m07

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class TerminosDeUso : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminos)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        setupButtons()
        checkIfAlreadyAccepted()
    }

    private fun setupButtons() {
        val backButton = findViewById<AppCompatButton>(R.id.buttonBack)
        val acceptButton = findViewById<AppCompatButton>(R.id.buttonAccept)

        backButton.setOnClickListener {
            finish()
        }

        acceptButton.setOnClickListener {
            acceptTerms()
        }
    }

    private fun checkIfAlreadyAccepted() {
        val hasAcceptedTerms = sharedPreferences.getBoolean("terms_accepted", false)
        val acceptButton = findViewById<AppCompatButton>(R.id.buttonAccept)

        if (hasAcceptedTerms) {
            acceptButton.text = "✅ YA ACEPTADOS"
            acceptButton.isEnabled = false
            acceptButton.alpha = 0.6f
        }
    }

    private fun acceptTerms() {
        // Guardar que el usuario ha aceptado los términos
        sharedPreferences.edit()
            .putBoolean("terms_accepted", true)
            .putLong("terms_accepted_date", System.currentTimeMillis())
            .apply()

        // Mostrar mensaje de confirmación
        Toast.makeText(this, "Términos aceptados correctamente", Toast.LENGTH_SHORT).show()

        // Actualizar el botón
        val acceptButton = findViewById<AppCompatButton>(R.id.buttonAccept)
        acceptButton.text = "✅ YA ACEPTADOS"
        acceptButton.isEnabled = false
        acceptButton.alpha = 0.6f

        // Volver a la pantalla anterior después de un breve delay
        acceptButton.postDelayed({
            finish()
        }, 1500)
    }

    // Método estático para verificar si se han aceptado los términos
    companion object {
        fun hasUserAcceptedTerms(context: android.content.Context): Boolean {
            val prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
            return prefs.getBoolean("terms_accepted", false)
        }
    }
}