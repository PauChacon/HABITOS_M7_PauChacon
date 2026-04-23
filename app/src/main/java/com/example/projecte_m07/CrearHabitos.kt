package com.example.projecte_m07

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projecte_m07.habitos.HabitosAPI
import com.example.projecte_m07.habitos.HistorialCreate
import com.example.recyclerview.habitos.HabitoCreate
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CrearHabito : AppCompatActivity() {

    private lateinit var inputHabitName: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var switchImportance: Switch
    private lateinit var textSelectedTime: TextView
    private lateinit var buttonSelectTime: ImageButton
    private lateinit var buttonCreateHabit: AppCompatButton

    private var selectedHour: Int = 8
    private var selectedMinute: Int = 0
    private val categories = arrayOf("Salud", "Productividad", "Ocio", "Bienestar", "Hogar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_habitos)

        initializeViews()
        setupSpinner()
        setupListeners()
        setupNavigation()
        updateTimeDisplay()
    }

    private fun initializeViews() {
        inputHabitName = findViewById(R.id.inputHabitName)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        switchImportance = findViewById(R.id.switchImportance)
        textSelectedTime = findViewById(R.id.textSelectedTime)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)
        buttonCreateHabit = findViewById(R.id.buttonCreateHabit)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<AppCompatButton>(R.id.buttonVolver).setOnClickListener {
            finish()
        }

        buttonSelectTime.setOnClickListener { showTimePickerDialog() }
        textSelectedTime.setOnClickListener { showTimePickerDialog() }
        buttonCreateHabit.setOnClickListener { createNewHabit() }
        setupSwitchColors()
    }

    private fun setupSwitchColors() {
        switchImportance.setOnCheckedChangeListener { _, isChecked ->
            val thumbColor = if (isChecked) "#4CAF50" else "#9E9E9E"
            val trackColor = if (isChecked) "#81C784" else "#E0E0E0"
            switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(thumbColor))
            switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(trackColor))
        }
        switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E"))
        switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E0E0E0"))
    }

    private fun setupNavigation() {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageView>(R.id.buttonMenu)

        val headerView = navigationView.getHeaderView(0)
        val headerTitle = headerView.findViewById<TextView>(R.id.header_title)
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val username = prefs.getString("username", "Usuario")
        headerTitle.text = getString(R.string.drawer_saludo, username)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> { startActivity(Intent(this, Ajustes::class.java)); true }
                R.id.nav_historial -> { startActivity(Intent(this, MenuHistorial::class.java)); true }
                else -> false
            }.also { drawerLayout.closeDrawer(GravityCompat.END) }
        }

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.END) }
    }

    private fun showTimePickerDialog() {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
            updateTimeDisplay()
        }, selectedHour, selectedMinute, true).show()
    }

    private fun updateTimeDisplay() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        textSelectedTime.text = timeFormat.format(calendar.time)
    }

    private fun createNewHabit() {
        val habitName = inputHabitName.text.toString().trim()
        val selectedCategory = spinnerCategory.selectedItem.toString()
        val isImportant = switchImportance.isChecked
        val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)

        if (habitName.isEmpty()) {
            inputHabitName.error = "El nombre del hábito es obligatorio"
            inputHabitName.requestFocus()
            return
        }

        if (habitName.length < 3) {
            inputHabitName.error = "El nombre debe tener al menos 3 caracteres"
            inputHabitName.requestFocus()
            return
        }

        val newHabit = HabitoCreate(habitName, selectedCategory, isImportant, timeString)
        buttonCreateHabit.isEnabled = false
        buttonCreateHabit.text = "Creando..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = getSharedPreferences("user_session", MODE_PRIVATE).getInt("user_id", -1)
                HabitosAPI.API().createHabito(userId, newHabit)

                HabitosAPI.API().addHistorial(userId, HistorialCreate(
                    tipo = "creado",
                    nombre_habito = habitName,
                    categoria = selectedCategory,
                    importante = isImportant,
                    hora = timeString
                )
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearHabito, "Hábito '$habitName' creado", Toast.LENGTH_SHORT).show()
                    clearForm()
                    buttonCreateHabit.isEnabled = true
                    buttonCreateHabit.text = "CREAR HÁBITO"
                }
            } catch (e: Exception) {
                Log.e("CrearHabito", "Error al crear hábito", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CrearHabito, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    buttonCreateHabit.isEnabled = true
                    buttonCreateHabit.text = "CREAR HÁBITO"
                }
            }
        }
    }

    private fun clearForm() {
        inputHabitName.text.clear()
        inputHabitName.error = null
        spinnerCategory.setSelection(0)
        switchImportance.isChecked = false
        selectedHour = 8
        selectedMinute = 0
        updateTimeDisplay()
        switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E"))
        switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E0E0E0"))
    }
}