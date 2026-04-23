package com.example.projecte_m07

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.projecte_m07.habitos.HistorialEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuHistorial : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_historial)

        recyclerView = findViewById(R.id.recyclerHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

        loadHistorial()
    }

    private fun loadHistorial() {
        val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val historial = HabitosAPI.API().getHistorial(userId)
                withContext(Dispatchers.Main) {
                    if (historial.isEmpty()) {
                        Toast.makeText(this@MenuHistorial, "No hay entradas en el historial", Toast.LENGTH_SHORT).show()
                    }
                    recyclerView.adapter = HistorialAdapter(historial)
                }
            } catch (e: Exception) {
                Log.e("MenuHistorial", "Error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuHistorial, "Error al cargar historial", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistorial()
    }
}

class HistorialAdapter(
    private val lista: List<HistorialEntry>
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val indicador: View = view.findViewById(R.id.indicador)
        val tvAccion: TextView = view.findViewById(R.id.tvAccion)
        val tvDetalle: TextView = view.findViewById(R.id.tvDetalle)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entrada = lista[position]

        if (entrada.tipo == "creado") {
            holder.indicador.setBackgroundColor(Color.parseColor("#4CAF50"))
            holder.tvAccion.setTextColor(Color.parseColor("#4CAF50"))
            holder.tvAccion.text = "✓ Creado: ${entrada.nombre_habito}"
        } else {
            holder.indicador.setBackgroundColor(Color.parseColor("#F44336"))
            holder.tvAccion.setTextColor(Color.parseColor("#F44336"))
            holder.tvAccion.text = "✕ Borrado: ${entrada.nombre_habito}"
        }

        val importanteText = if (entrada.importante) " · Importante" else ""
        holder.tvDetalle.text = "${entrada.categoria} · ${entrada.hora}$importanteText"
        holder.tvFecha.text = entrada.fecha
    }

    override fun getItemCount(): Int = lista.size
}