package com.example.projecte_m07

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.recyclerview.habitos.Habito
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GraficoHabitos : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private val categorias = arrayOf("Salud", "Productividad", "Ocio", "Bienestar", "Hogar")
    private val coloresBarras = intArrayOf(
        Color.parseColor("#4CAF50"), // Verde - Salud
        Color.parseColor("#2196F3"), // Azul - Productividad
        Color.parseColor("#FF9800"), // Naranja - Ocio
        Color.parseColor("#9C27B0"), // Morado - Bienestar
        Color.parseColor("#F44336")  // Rojo - Hogar
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico_habitos)

        initializeViews()
        setupChart()
        loadDataAndUpdateChart()
    }

    private fun initializeViews() {
        barChart = findViewById(R.id.barChart)

        findViewById<Button>(R.id.buttonVolver).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonRefresh).setOnClickListener {
            loadDataAndUpdateChart()
        }
    }

    private fun setupChart() {
        barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setPinchZoom(false)
            setScaleEnabled(false)

            // Configurar eje X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = categorias.size
                valueFormatter = IndexAxisValueFormatter(categorias)
                textSize = 12f
                textColor = Color.BLACK
            }

            // Configurar eje Y izquierdo
            axisLeft.apply {
                setDrawGridLines(true)
                granularity = 1f
                axisMinimum = 0f
                textSize = 12f
                textColor = Color.BLACK
            }

            // Desactivar eje Y derecho
            axisRight.isEnabled = false

            // Configurar leyenda
            legend.isEnabled = false

            // Animación
            animateY(1000)
        }
    }

    private fun loadDataAndUpdateChart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitos = HabitosAPI.API().getHabitos()

                withContext(Dispatchers.Main) {
                    updateChart(habitos)
                }
            } catch (e: Exception) {
                Log.e("GraficoHabitos", "Error al cargar hábitos", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GraficoHabitos, "Error al cargar datos del gráfico", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateChart(habitos: List<Habito>) {
        // Contar hábitos por categoría
        val conteosPorCategoria = mutableMapOf<String, Int>()

        // Inicializar todas las categorías con 0
        categorias.forEach { categoria ->
            conteosPorCategoria[categoria] = 0
        }

        // Contar los hábitos existentes
        habitos.forEach { habito ->
            val categoria = habito.categoria
            if (categoria in categorias) {
                conteosPorCategoria[categoria] = conteosPorCategoria[categoria]!! + 1
            }
        }

        // Crear entradas para el gráfico
        val entries = mutableListOf<BarEntry>()
        categorias.forEachIndexed { index, categoria ->
            val count = conteosPorCategoria[categoria] ?: 0
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
        }

        // Crear dataset
        val dataSet = BarDataSet(entries, "Hábitos por Categoría").apply {
            colors = coloresBarras.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK

            // Formatear valores para mostrar solo enteros
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "" else value.toInt().toString()
                }
            }
        }

        // Aplicar datos al gráfico
        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.data = barData
        barChart.invalidate() // Refrescar el gráfico

        // Mostrar estadísticas en Toast
        val totalHabitos = habitos.size
        val categoriaConMas = conteosPorCategoria.maxByOrNull { it.value }

        val mensaje = if (totalHabitos > 0) {
            "Total: $totalHabitos hábitos\nCategoría principal: ${categoriaConMas?.key} (${categoriaConMas?.value})"
        } else {
            "No hay hábitos registrados"
        }

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        loadDataAndUpdateChart()
    }
}