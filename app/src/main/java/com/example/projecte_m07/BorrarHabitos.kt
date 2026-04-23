package com.example.projecte_m07

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.projecte_m07.habitos.HistorialCreate
import com.example.recyclerview.habitos.Habito
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BorrarHabitos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrar_habitos)

        recyclerView = findViewById(R.id.recyclerBorrarHabitos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<AppCompatButton>(R.id.buttonVolver).setOnClickListener {
            finish()
        }

        loadHabitos()
    }

    private fun loadHabitos() {
        val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitos = HabitosAPI.API().getHabitos(userId)
                withContext(Dispatchers.Main) {
                    recyclerView.adapter = BorrarHabitosAdapter(habitos.toMutableList()) { habito ->
                        mostrarDialogoEliminar(habito)
                    }
                }
            } catch (e: Exception) {
                Log.e("BorrarHabitos", "Error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BorrarHabitos, "Error al cargar hábitos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEliminar(habito: Habito) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar hábito")
            .setMessage("¿Seguro que quieres eliminar '${habito.nombre}'?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        HabitosAPI.API().deleteHabito(habito.id)

                        val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
                        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                        HabitosAPI.API().addHistorial(userId, HistorialCreate(
                            tipo = "borrado",
                            nombre_habito = habito.nombre,
                            categoria = habito.categoria,
                            importante = habito.importante,
                            hora = if (habito.hora != null) sdf.format(habito.hora) else "00:00"
                        )
                        )

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@BorrarHabitos, "'${habito.nombre}' eliminado", Toast.LENGTH_SHORT).show()
                            loadHabitos()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@BorrarHabitos, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadHabitos()
    }
}

class BorrarHabitosAdapter(
    private val listaHabitos: MutableList<Habito>,
    private val onDeleteClick: (Habito) -> Unit
) : RecyclerView.Adapter<BorrarHabitosAdapter.ViewHolder>() {

    private val categoriaToImagen: Map<String, Int> = mapOf(
        "Salud" to R.drawable.ic_salud,
        "Productividad" to R.drawable.ic_trabajo,
        "Hogar" to R.drawable.ic_hogar,
        "Bienestar" to R.drawable.ic_bienestar,
        "Ocio" to R.drawable.ic_ocio
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val ivCategoria: ImageView = view.findViewById(R.id.ivCategoria)
        val cardView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val habito = listaHabitos[position]

        holder.tvNombre.text = habito.nombre
        holder.tvNombre.setTextColor(
            if (habito.importante) Color.parseColor("#FF0000") else Color.BLACK
        )

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvHora.text = if (habito.hora != null) sdf.format(habito.hora) else ""

        val resId = categoriaToImagen[habito.categoria]
        if (resId != null) {
            holder.ivCategoria.setImageResource(resId)
            holder.ivCategoria.visibility = View.VISIBLE
        } else {
            holder.ivCategoria.visibility = View.GONE
        }

        // Toque en la tarjeta = borrar
        holder.cardView.setOnClickListener {
            onDeleteClick(habito)
        }

        // Tinte rojo en el icono para indicar modo borrar
        holder.ivCategoria.imageTintList = ColorStateList.valueOf(Color.parseColor("#F44336"))
    }

    override fun getItemCount(): Int = listaHabitos.size
}