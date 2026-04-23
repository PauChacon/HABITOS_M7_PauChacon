package com.example.projecte_m07.habitos

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projecte_m07.R
import com.example.recyclerview.habitos.Habito
import java.text.SimpleDateFormat
import java.util.*

class HabitoAdapter(
    private val listaHabitos: MutableList<Habito>,
    private val onItemClick: (Habito, Int) -> Unit
) : RecyclerView.Adapter<HabitoAdapter.HabitoViewHolder>() {

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
        val cardView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habito, parent, false)
        return HabitoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitoViewHolder, position: Int) {
        val habito = listaHabitos[position]

        holder.tvNombre.text = habito.nombre

        if (habito.importante) {
            holder.tvNombre.setTextColor(Color.parseColor("#FF0000"))
        } else {
            holder.tvNombre.setTextColor(Color.BLACK)
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvHora.text = if (habito.hora != null) sdf.format(habito.hora) else ""

        val resId = categoriaToImagen[habito.categoria]
        if (resId != null) {
            holder.ivCategoria.setImageResource(resId)
            holder.ivCategoria.visibility = View.VISIBLE
        } else {
            holder.ivCategoria.visibility = View.GONE
        }

        aplicarEstadoCompletado(holder, habito.completado)

        holder.cardView.setOnClickListener {
            onItemClick(habito, position)
        }
    }

    private fun aplicarEstadoCompletado(holder: HabitoViewHolder, completado: Boolean) {
        if (completado) {
            holder.tvNombre.paintFlags = holder.tvNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvNombre.setTextColor(Color.parseColor("#A0A0A0"))
            holder.cardView.alpha = 0.6f
            holder.ivCategoria.imageTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
        } else {
            holder.tvNombre.paintFlags = holder.tvNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.cardView.alpha = 1.0f
            holder.ivCategoria.imageTintList = null
        }
    }

    override fun getItemCount(): Int = listaHabitos.size
}