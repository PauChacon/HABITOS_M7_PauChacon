package com.example.projecte_m07.habitos

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projecte_m07.EditarHabitoDetalle
import com.example.projecte_m07.R
import com.example.recyclerview.habitos.Habito
import java.text.SimpleDateFormat
import java.util.*

class HabitoAdapter(private val listaHabitos: List<Habito>) : RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder>() {

    private val categoriaToImagen: Map<String, Int> = mapOf(
        "Salud" to R.drawable.ic_salud,
        "Productividad" to R.drawable.ic_trabajo,
        "Hogar" to R.drawable.ic_hogar,
        "Bienestar" to R.drawable.ic_bienestar,
        "Ocio" to R.drawable.ic_ocio
    )

    class HabitoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val ivCategoria: ImageView = view.findViewById(R.id.ivCategoria)
        val cardView: View = view // Para el click en toda la tarjeta
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habito, parent, false)
        return HabitoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitoViewHolder, position: Int) {
        val habito = listaHabitos[position]

        holder.tvNombre.text = habito.nombre

        // Solo cambiar color según importancia
        if (habito.importante) {
            holder.tvNombre.setTextColor(Color.parseColor("#FF0000"))
        } else {
            holder.tvNombre.setTextColor(Color.BLACK)
        }

        // Hora formateada
        val hora = habito.hora
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvHora.text = if (hora != null) sdf.format(hora) else ""

        // Imagen de categoría
        val resId = categoriaToImagen[habito.categoria]
        if (resId != null) {
            holder.ivCategoria.setImageResource(resId)
            holder.ivCategoria.visibility = View.VISIBLE
        } else {
            holder.ivCategoria.visibility = View.GONE
        }

        // Click listener para toda la tarjeta
        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditarHabitoDetalle::class.java).apply {
                putExtra("HABITO_ID", habito.id)
                putExtra("HABITO_NOMBRE", habito.nombre)
                putExtra("HABITO_CATEGORIA", habito.categoria)
                putExtra("HABITO_IMPORTANTE", habito.importante)
                putExtra("HABITO_HORA", if (hora != null) sdf.format(hora) else "08:00")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaHabitos.size
}