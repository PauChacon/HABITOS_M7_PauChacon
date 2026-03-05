package com.example.projecte_m07

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Register : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.editTextRegisterUsername)
        email = findViewById(R.id.editTextRegisterEmail)
        phone = findViewById(R.id.editTextRegisterPhone)
        password = findViewById(R.id.editTextRegisterPassword)
        confirmPassword = findViewById(R.id.editTextRegisterConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonConfirmRegister)
        errorText = findViewById(R.id.textViewRegisterError)

        applyDefaultStyle(username)
        applyDefaultStyle(email)
        applyDefaultStyle(phone)
        applyDefaultStyle(password)
        applyDefaultStyle(confirmPassword)


        buttonRegister.setOnClickListener {
            handleRegister()
        }
    }
    private fun applyDefaultStyle(field: EditText) {
        val drawable = GradientDrawable()
        drawable.setStroke(3, Color.GRAY)  // Borde gris
        drawable.cornerRadius = 8f
        field.background = drawable
    }




    private fun handleRegister() {
        val user = username.text.toString().trim()
        val mail = email.text.toString().trim()
        val tel = phone.text.toString().trim()
        val pass = password.text.toString()
        val confirm = confirmPassword.text.toString()

        var isValid = true

        // Reset fields
        resetField(username)
        resetField(email)
        resetField(phone)
        resetField(password)
        resetField(confirmPassword)
        errorText.visibility = TextView.GONE

        // Validations
        if (user.length <= 3) {
            showError("El nombre de usuario debe tener más de 3 caracteres", username)
            isValid = false
        }else if(user.length>=26){
            showError("El nombre de usuario debe tener menos de 25 caracteres", username)
            isValid = false

        } else if (!tel.matches(Regex("^\\d{9}$"))) {
            showError("El número de teléfono debe tener exactamente 9 cifras", phone)
            isValid = false
        } else if (!mail.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))) {
            showError("Introduce un correo electrónico válido", email)
            isValid = false
        } else if (!isStrongPassword(pass)) {
            showError("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo", password)
            isValid = false
        } else if (pass != confirm) {
            showError("Las contraseñas no coinciden", confirmPassword)
            isValid = false
        }

        if (isValid) {
            Toast.makeText(this, "Registro correcto", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)        }
    }

    private fun resetField(field: EditText) {
        applyDefaultStyle(field)
    }


    private fun showError(message: String, field: EditText) {
        errorText.text = message
        errorText.visibility = TextView.VISIBLE
        highlightFieldError(field)
    }

    private fun highlightFieldError(field: EditText) {
        val drawable = GradientDrawable()
        drawable.setStroke(3, Color.RED)
        drawable.cornerRadius = 8f
        field.background = drawable
    }

    private fun isStrongPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")
        return regex.matches(password)
    }
}
