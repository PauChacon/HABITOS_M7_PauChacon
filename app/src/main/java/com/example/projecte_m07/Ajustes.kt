package com.example.projecte_m07

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projecte_m07.habitos.HabitosAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Ajustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        findViewById<android.view.View>(R.id.perfil_layout).setOnClickListener {
            startActivity(Intent(this, EditarPerfil::class.java))
        }

        // Cerrar sesión
        findViewById<android.view.View>(R.id.logout_layout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Borrar cuenta
        findViewById<android.view.View>(R.id.borrar_cuenta_layout).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Borrar cuenta")
                .setMessage("¿Estás seguro? Se eliminará tu cuenta y todos tus datos permanentemente. Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, borrar") { _, _ ->
                    borrarCuenta()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Volver
        findViewById<android.view.View>(R.id.btn_volver).setOnClickListener {
            finish()
        }
    }

    private fun borrarCuenta() {
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "Error: no se encontró la sesión", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                HabitosAPI.API().deleteUsuario(userId)

                withContext(Dispatchers.Main) {
                    prefs.edit().clear().apply()
                    Toast.makeText(this@Ajustes, "Cuenta eliminada", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@Ajustes, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Ajustes, "Error al borrar cuenta: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}