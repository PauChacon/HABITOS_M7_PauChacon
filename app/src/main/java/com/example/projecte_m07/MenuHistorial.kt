package com.example.projecte_m07

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.projecte_m07.habitos.PruebaAPI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MenuHistorial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_historial)



        // Menú inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, Ajustes::class.java))
                    true
                }
                R.id.nav_terms -> {
                    startActivity(Intent(this, TerminosDeUso::class.java))
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, Menu::class.java))
                    true
                }
                R.id.nav_grafico -> {
                    startActivity(Intent(this, GraficoHabitos::class.java))
                    true
                }
                else -> false
            }
        }


    }
}
