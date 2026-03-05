package com.example.projecte_m07


import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val logoImageView = findViewById<ImageView>(R.id.logoImage)

        val scaleAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0f, 1f)
        scaleAnimator.duration = 2000
        scaleAnimator.start()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonGoRegister = findViewById<TextView>(R.id.buttonGoRegister)

        buttonLogin.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        buttonGoRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}
