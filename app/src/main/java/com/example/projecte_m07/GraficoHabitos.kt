package com.example.projecte_m07

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.recyclerview.habitos.Habito
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GraficoHabitos : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var textTitulo: TextView
    private lateinit var textSubtitulo: TextView
    private var currentChart = 0
    private var allHabitos: List<Habito> = emptyList()

    private val categorias = arrayOf("Salud", "Productividad", "Ocio", "Bienestar", "Hogar")
    private val coloresBarras = intArrayOf(
        Color.parseColor("#4CAF50"),
        Color.parseColor("#2196F3"),
        Color.parseColor("#FF9800"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#F44336")
    )

    private val chartTitles = arrayOf(
        "Hábitos por Categoría",
        "Hábitos Importantes vs Normal",
        "Hábitos por Franja Horaria",
        "Resumen General"
    )

    private val chartSubtitles = arrayOf(
        "Cantidad de hábitos por categoría",
        "Comparación por nivel de importancia",
        "Distribución por hora del día",
        "Estadísticas generales de tus hábitos"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico_habitos)

        initializeViews()
        setupChart()
        setupBottomNav()
        loadDataAndUpdateChart()
    }

    private fun initializeViews() {
        barChart = findViewById(R.id.barChart)
        textTitulo = findViewById(R.id.textTituloGrafico)
        textSubtitulo = findViewById(R.id.textSubtitulo)

        findViewById<ImageButton>(R.id.buttonRefresh).setOnClickListener {
            loadDataAndUpdateChart()
        }

        findViewById<ImageButton>(R.id.buttonPrev).setOnClickListener {
            currentChart = if (currentChart == 0) chartTitles.size - 1 else currentChart - 1
            updateChartDisplay()
        }

        findViewById<ImageButton>(R.id.buttonNext).setOnClickListener {
            currentChart = (currentChart + 1) % chartTitles.size
            updateChartDisplay()
        }
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_grafico
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, Menu::class.java))
                    finish()
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, MenuHistorial::class.java))
                    finish()
                    true
                }
                R.id.nav_grafico -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, Ajustes::class.java))
                    true
                }
                else -> false
            }
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

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textSize = 12f
                textColor = Color.BLACK
            }

            axisLeft.apply {
                setDrawGridLines(true)
                granularity = 1f
                axisMinimum = 0f
                textSize = 12f
                textColor = Color.BLACK
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
        }
    }

    private fun loadDataAndUpdateChart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
                val habitos = HabitosAPI.API().getHabitos(userId)

                withContext(Dispatchers.Main) {
                    allHabitos = habitos
                    updateChartDisplay()
                }
            } catch (e: Exception) {
                Log.e("GraficoHabitos", "Error al cargar hábitos", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GraficoHabitos, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateChartDisplay() {
        textTitulo.text = chartTitles[currentChart]
        textSubtitulo.text = chartSubtitles[currentChart]

        when (currentChart) {
            0 -> showCategoryChart()
            1 -> showImportanceChart()
            2 -> showTimeChart()
            3 -> showSummaryChart()
        }
    }

    private fun showCategoryChart() {
        val conteos = mutableMapOf<String, Int>()
        categorias.forEach { conteos[it] = 0 }
        allHabitos.forEach { h ->
            if (h.categoria in categorias) conteos[h.categoria] = conteos[h.categoria]!! + 1
        }

        val entries = categorias.mapIndexed { i, cat ->
            BarEntry(i.toFloat(), (conteos[cat] ?: 0).toFloat())
        }

        barChart.xAxis.labelCount = categorias.size
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(categorias)

        val dataSet = BarDataSet(entries, "").apply {
            colors = coloresBarras.toList()
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            valueFormatter = intValueFormatter()
        }

        barChart.data = BarData(dataSet).apply { barWidth = 0.6f }
        barChart.invalidate()
        barChart.animateY(800)
    }

    private fun showImportanceChart() {
        val importantes = allHabitos.count { it.importante }
        val normales = allHabitos.size - importantes
        val labels = arrayOf("Importantes", "Normales")

        val entries = listOf(
            BarEntry(0f, importantes.toFloat()),
            BarEntry(1f, normales.toFloat())
        )

        barChart.xAxis.labelCount = 2
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val dataSet = BarDataSet(entries, "").apply {
            colors = listOf(Color.parseColor("#F44336"), Color.parseColor("#4CAF50"))
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            valueFormatter = intValueFormatter()
        }

        barChart.data = BarData(dataSet).apply { barWidth = 0.5f }
        barChart.invalidate()
        barChart.animateY(800)
    }

    private fun showTimeChart() {
        val franjas = arrayOf("Mañana\n(6-12)", "Tarde\n(12-18)", "Noche\n(18-24)", "Madrugada\n(0-6)")
        val conteos = intArrayOf(0, 0, 0, 0)

        allHabitos.forEach { h ->
            h.hora?.let {
                val cal = java.util.Calendar.getInstance()
                cal.time = it
                val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                when {
                    hour in 6..11 -> conteos[0]++
                    hour in 12..17 -> conteos[1]++
                    hour in 18..23 -> conteos[2]++
                    else -> conteos[3]++
                }
            }
        }

        val entries = conteos.mapIndexed { i, count ->
            BarEntry(i.toFloat(), count.toFloat())
        }

        barChart.xAxis.labelCount = 4
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(franjas)

        val dataSet = BarDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#FF9800"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#607D8B")
            )
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            valueFormatter = intValueFormatter()
        }

        barChart.data = BarData(dataSet).apply { barWidth = 0.5f }
        barChart.invalidate()
        barChart.animateY(800)
    }

    private fun showSummaryChart() {
        val total = allHabitos.size.toFloat()
        val importantes = allHabitos.count { it.importante }.toFloat()
        val conHora = allHabitos.count { it.hora != null }.toFloat()
        val categoriasDist = allHabitos.map { it.categoria }.distinct().size.toFloat()

        val labels = arrayOf("Total", "Importantes", "Con hora", "Categorías")
        val entries = listOf(
            BarEntry(0f, total),
            BarEntry(1f, importantes),
            BarEntry(2f, conHora),
            BarEntry(3f, categoriasDist)
        )

        barChart.xAxis.labelCount = 4
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val dataSet = BarDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#F44336"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#FF9800")
            )
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            valueFormatter = intValueFormatter()
        }

        barChart.data = BarData(dataSet).apply { barWidth = 0.5f }
        barChart.invalidate()
        barChart.animateY(800)
    }

    private fun intValueFormatter() = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return if (value == 0f) "" else value.toInt().toString()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDataAndUpdateChart()
    }
}