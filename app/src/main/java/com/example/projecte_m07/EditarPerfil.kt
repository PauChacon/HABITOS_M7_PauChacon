package com.example.projecte_m07

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.projecte_m07.habitos.UsuarioUpdateRequest
import com.example.projecte_m07.viewmodel.EditarPerfilViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarPerfil : AppCompatActivity() {

    private lateinit var inputUsername: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputTelefono: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        inputUsername = findViewById(R.id.inputUsername)
        inputEmail = findViewById(R.id.inputEmail)
        inputTelefono = findViewById(R.id.inputTelefono)

        // Cargar datos actuales de la sesión
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        inputUsername.setText(prefs.getString("username", ""))
        inputEmail.setText(prefs.getString("email", ""))
        inputTelefono.setText(prefs.getString("telefono", ""))

        findViewById<AppCompatButton>(R.id.buttonGuardar).setOnClickListener {
            guardarCambios()
        }

        findViewById<AppCompatButton>(R.id.buttonVolver).setOnClickListener {
            finish()
        }
    }

    private val viewModel = EditarPerfilViewModel()

    private fun guardarCambios() {
        val username = inputUsername.text.toString().trim()
        val email = inputEmail.text.toString().trim()
        val telefono = inputTelefono.text.toString().trim()

        if (!viewModel.validate(username, email, telefono)) {
            when {
                username.length <= 3 -> inputUsername.error = "El nombre debe tener más de 3 caracteres"
                username.length >= 26 -> inputUsername.error = "El nombre debe tener menos de 25 caracteres"
                !telefono.matches(Regex("^\\d{9}$")) -> inputTelefono.error = "El teléfono debe tener 9 cifras"
                !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) -> inputEmail.error = "Email no válido"
            }
            return
        }

        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().updateUsuario(userId, UsuarioUpdateRequest(username, email, telefono))

                withContext(Dispatchers.Main) {
                    prefs.edit()
                        .putString("username", response.username)
                        .putString("email", response.email)
                        .putString("telefono", response.telefono)
                        .apply()

                    Toast.makeText(this@EditarPerfil, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: retrofit2.HttpException) {
                withContext(Dispatchers.Main) {
                    val msg = if (e.code() == 400) "El usuario o email ya existe" else "Error al actualizar"
                    Toast.makeText(this@EditarPerfil, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarPerfil, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}