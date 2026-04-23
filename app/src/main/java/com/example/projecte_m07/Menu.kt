package com.example.projecte_m07

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecte_m07.habitos.HabitoAdapter
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.recyclerview.habitos.Habito
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class Menu : AppCompatActivity() {

    companion object {
        var isBannerClosed: Boolean = false
    }

    private lateinit var recyclerView: RecyclerView
    private var allHabits: List<Habito> = emptyList()
    private var currentFilter: FilterType = FilterType.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initializeViews()
        setupAnimations()
        setupNavigation()
        setupRecyclerView()
        setupFilterButton()
        loadHabitsFromAPI()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerHabitos)

        val saludos = listOf(
            "Hola", "Ey", "Qué tal", "Buenas", "Saludos", "Hey", "Bienvenid@", "Qué pasa",
            "Cómo andamos", "A darle caña", "Vamos allá", "De vuelta", "Qué hay", "Arrancamos", "Al lío"
        )
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = prefs.getString("username", "")
        val saludo = saludos.random()
        val textSaludo = findViewById<TextView>(R.id.textSaludo)
        textSaludo.text = "$saludo, $username!"
    }

    private fun setupAnimations() {
        val banner = findViewById<View>(R.id.habitExtraBanner)
        val floatAnim = AnimationUtils.loadAnimation(this, R.anim.floating)
        banner.startAnimation(floatAnim)

        if (isBannerClosed) {
            banner.visibility = View.GONE
        }

        val closeBannerBtn = findViewById<ImageButton>(R.id.closeBannerBtn)
        closeBannerBtn.setOnClickListener {
            banner.clearAnimation()
            banner.visibility = View.INVISIBLE
            banner.isClickable = false
            banner.isFocusable = false
            isBannerClosed = true
        }

        findViewById<View>(R.id.habitExtraText).setOnClickListener {
            startActivity(Intent(this, CrearHabito::class.java))
        }
    }

    private fun setupNavigation() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageView>(R.id.buttonMenu)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val editarHabitosButton = findViewById<Button>(R.id.buttonEditHabits)

        val headerView = navigationView.getHeaderView(0)
        val headerTitle = headerView.findViewById<TextView>(R.id.header_title)
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = prefs.getString("username", "Usuario")
        headerTitle.text = getString(R.string.drawer_saludo, username)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> { startActivity(Intent(this, Ajustes::class.java)); true }
                R.id.nav_terms -> { startActivity(Intent(this, TerminosDeUso::class.java)); true }
                R.id.nav_historial -> { startActivity(Intent(this, MenuHistorial::class.java)); true }
                R.id.nav_grafico -> { startActivity(Intent(this, GraficoHabitos::class.java)); true }
                else -> false
            }.also { drawerLayout.closeDrawer(GravityCompat.END) }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_inicio
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_settings -> { startActivity(Intent(this, Ajustes::class.java)); true }
                R.id.nav_historial -> { startActivity(Intent(this, MenuHistorial::class.java)); true }
                R.id.nav_grafico -> { startActivity(Intent(this, GraficoHabitos::class.java)); true }
                else -> false
            }
        }

        val borrarHabitosButton = findViewById<Button>(R.id.buttonDeleteHabits)
        borrarHabitosButton.setOnClickListener {
            startActivity(Intent(this, BorrarHabitos::class.java))
        }

        editarHabitosButton.setOnClickListener {
            startActivity(Intent(this, CrearHabito::class.java))
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = null
    }

    private fun setupFilterButton() {
        val filterButton = findViewById<ImageButton>(R.id.buttonFilter)
        filterButton.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun applyFilter() {
        val filteredHabits = when (currentFilter) {
            FilterType.NONE -> allHabits
            FilterType.BY_TIME -> sortByTime(allHabits)
            FilterType.BY_IMPORTANCE -> sortByImportance(allHabits)
        }

        val scrollState = recyclerView.layoutManager?.onSaveInstanceState()
        recyclerView.adapter = HabitoAdapter(filteredHabits.toMutableList()) { habito, position ->
            marcarCompletado(habito, position)
        }
        recyclerView.layoutManager?.onRestoreInstanceState(scrollState)
    }

    private fun marcarCompletado(habito: Habito, position: Int) {
        val nuevoEstado = !habito.completado
        allHabits = allHabits.toMutableList().also { it[position] = habito.copy(completado = nuevoEstado) }
        applyFilter()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                HabitosAPI.API().completarHabito(habito.id, mapOf("completado" to nuevoEstado))
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    allHabits = allHabits.toMutableList().also { it[position] = habito }
                    applyFilter()
                }
            }
        }
    }

    private fun showFilterDialog() {
        val filterOptions = FilterType.values().map { it.displayName }.toTypedArray()
        val currentIndex = FilterType.values().indexOf(currentFilter)

        android.app.AlertDialog.Builder(this)
            .setTitle("🔽 Filtrar hábitos")
            .setSingleChoiceItems(filterOptions, currentIndex) { dialog, which ->
                currentFilter = FilterType.values()[which]
                applyFilter()
                dialog.dismiss()
                Toast.makeText(this, "Filtro aplicado: ${currentFilter.displayName}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun sortByTime(habits: List<Habito>): List<Habito> {
        return habits.sortedBy { habito ->
            habito.hora?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
            } ?: Int.MAX_VALUE
        }
    }

    private fun sortByImportance(habits: List<Habito>): List<Habito> {
        return habits.sortedWith { a, b ->
            when {
                a.importante && !b.importante -> -1
                !a.importante && b.importante -> 1
                else -> 0
            }
        }
    }

    private fun loadHabitsFromAPI() {
        val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().getHabitos(userId)
                withContext(Dispatchers.Main) {
                    allHabits = response
                    applyFilter()
                    Log.d("Menu", "Hábitos cargados: ${response.size}")
                }
            } catch (e: Exception) {
                Log.e("Menu", "Error al cargar hábitos", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Menu, "Error al cargar hábitos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshHabitsList()
    }

    private fun refreshHabitsList() {
        val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().getHabitos(userId)
                withContext(Dispatchers.Main) {
                    allHabits = response
                    applyFilter()
                    Log.d("Menu", "Lista de hábitos refrescada: ${response.size} hábitos")
                }
            } catch (e: Exception) {
                Log.e("Menu", "Error al refrescar hábitos", e)
            }
        }
    }
}