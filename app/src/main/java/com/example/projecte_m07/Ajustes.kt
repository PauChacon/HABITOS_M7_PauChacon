package com.example.projecte_m07

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Ajustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        // Botón principal que existe en tu XML
        val guardarButton = findViewById<Button>(R.id.btn_guardar)
        guardarButton.setOnClickListener {
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Los switches que tienes en tu XML (solo para que no den error)
        val switchRecordatorios = findViewById<Switch>(R.id.switch_recordatorios)
        val switchTema = findViewById<Switch>(R.id.switch_tema)

        // Listeners básicos para los switches (solo muestran toast)
        switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            val mensaje = if (isChecked) "Recordatorios activados" else "Recordatorios desactivados"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }

        switchTema.setOnCheckedChangeListener { _, isChecked ->
            val mensaje = if (isChecked) "Tema oscuro activado" else "Tema claro activado"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}