package com.example.projecte_m07

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuHistorial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_historial)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_historial
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, Menu::class.java))
                    finish()
                    true
                }
                R.id.nav_historial -> true
                R.id.nav_grafico -> {
                    startActivity(Intent(this, GraficoHabitos::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, Ajustes::class.java))
                    true
                }
                else -> false
            }
        }
    }
}