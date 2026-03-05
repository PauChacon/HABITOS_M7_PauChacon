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
import com.example.projecte_m07.habitos.PruebaAPI
import com.example.recyclerview.habitos.Habito
import com.example.recyclerview.habitos.HabitoCreate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
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
        insertDefaultHabitsIfNeeded()
        setupNavigation()
        setupRecyclerView()
        setupFilterButton()
        loadHabitsFromAPI()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerHabitos)
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
            showAddHabitDialog()
        }
    }

    private fun insertDefaultHabitsIfNeeded() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val firstInsertDone = prefs.getBoolean("habitos_insertados", false)

        if (!firstInsertDone) {
            val listaHabitos = listOf(
                HabitoCreate("Hacer ejercicio", "Salud", true, "07:30"),
                HabitoCreate("Leer un libro", "Ocio", false, "21:00"),
                HabitoCreate("Limpiar la casa", "Productividad", true, "10:00"),
                HabitoCreate("Trabajar en proyecto", "Productividad", true, "09:00"),
                HabitoCreate("Meditar", "Salud", false, "06:30")
            )

            CoroutineScope(Dispatchers.IO).launch {
                listaHabitos.forEach { habito ->
                    try {
                        HabitosAPI.API().createHabito(habito)
                        Log.d("Menu", "Hábito insertado: ${habito.nombre}")
                    } catch (e: Exception) {
                        Log.e("Menu", "Error al insertar hábito: ${habito.nombre}", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    prefs.edit().putBoolean("habitos_insertados", true).apply()
                    loadHabitsFromAPI()
                }
            }
        }
    }

    private fun setupNavigation() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageView>(R.id.buttonMenu)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val editarHabitosButton = findViewById<Button>(R.id.buttonEditHabits)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
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
                    startActivity(Intent(this, MenuHistorial::class.java))
                    true
                }

                R.id.nav_grafico -> {
                    startActivity(Intent(this, GraficoHabitos::class.java))
                    true
                }

                R.id.nav_prueba -> {
                    startActivity(Intent(this, PruebaAPI::class.java))
                    true
                }

                else -> false
            }.also {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

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
                    startActivity(Intent(this, MenuHistorial::class.java))
                    true
                }

                R.id.nav_grafico -> {
                    startActivity(Intent(this, GraficoHabitos::class.java))
                    true
                }

                else -> false
            }
        }

        editarHabitosButton.setOnClickListener {
            val intent = Intent(this, EditarHabitos::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFilterButton() {
        val filterButton = findViewById<ImageButton>(R.id.buttonFilter)
        filterButton.setOnClickListener {
            showFilterDialog()
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

                val filterName = currentFilter.displayName
                Toast.makeText(this, "Filtro aplicado: $filterName", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun applyFilter() {
        val filteredHabits = when (currentFilter) {
            FilterType.NONE -> allHabits
            FilterType.BY_TIME -> sortByTime(allHabits)
            FilterType.BY_IMPORTANCE -> sortByImportance(allHabits)
        }

        recyclerView.adapter = HabitoAdapter(filteredHabits)
    }

    private fun sortByTime(habits: List<Habito>): List<Habito> {
        return habits.sortedBy { habito ->
            habito.hora?.let { hora ->
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                try {
                    val calendar = Calendar.getInstance()
                    calendar.time = hora
                    calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
                } catch (e: Exception) {
                    Int.MAX_VALUE // Si hay error, poner al final
                }
            } ?: Int.MAX_VALUE // Si no tiene hora, poner al final
        }
    }

    private fun sortByImportance(habits: List<Habito>): List<Habito> {
        return habits.sortedWith { a, b ->
            when {
                a.importante && !b.importante -> -1 // a va primero
                !a.importante && b.importante -> 1  // b va primero
                else -> 0 // mantener orden original si ambos son iguales
            }
        }
    }

    private fun loadHabitsFromAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().getHabitos()
                withContext(Dispatchers.Main) {
                    allHabits = response
                    applyFilter() // Aplicar el filtro actual
                    Log.d("Menu", "Hábitos cargados: ${response.size}")
                }
            } catch (e: Exception) {
                Log.e("Menu", "Error al cargar hábitos", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Menu,
                        "Error al cargar hábitos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showAddHabitDialog() {
        val intent = Intent(this, EditarHabitos::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshHabitsList()
    }

    private fun refreshHabitsList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().getHabitos()
                withContext(Dispatchers.Main) {
                    allHabits = response
                    applyFilter() // Mantener el filtro aplicado
                    Log.d("Menu", "Lista de hábitos refrescada: ${response.size} hábitos")
                }
            } catch (e: Exception) {
                Log.e("Menu", "Error al refrescar hábitos", e)
            }
        }
    }


}