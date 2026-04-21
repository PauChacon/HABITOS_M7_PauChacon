package com.example.projecte_m07

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.projecte_m07.habitos.UsuarioLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Si ya hay sesión abierta, ir directo al menú
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        if (prefs.getInt("user_id", -1) != -1) {
            startActivity(Intent(this, Menu::class.java))
            finish()
            return
        }

        val logoImageView = findViewById<ImageView>(R.id.logoImage)

        val scaleAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0f, 1f)
        scaleAnimator.duration = 2000
        scaleAnimator.start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputUsername = findViewById<EditText>(R.id.username)
        val inputPassword = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonGoRegister = findViewById<TextView>(R.id.buttonGoRegister)

        buttonLogin.setOnClickListener {
            val user = inputUsername.text.toString().trim()
            val pass = inputPassword.text.toString()

            // no validamos formato porque si el usuario existe ya pasó por el register
            // y si no existe pues simplemente no entra y punto
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Rellena usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginData = UsuarioLogin(user, pass)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = HabitosAPI.API().login(loginData)

                    withContext(Dispatchers.Main) {
                        // Guardar datos del usuario en la sesión
                        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                        prefs.edit()
                            .putInt("user_id", response.id)
                            .putString("username", response.username)
                            .putString("email", response.email)
                            .putString("telefono", response.telefono)
                            .apply()

                        Toast.makeText(this@MainActivity, "Bienvenido, ${response.username}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, Menu::class.java)
                        startActivity(intent)
                    }
                } catch (e: retrofit2.HttpException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        buttonGoRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}