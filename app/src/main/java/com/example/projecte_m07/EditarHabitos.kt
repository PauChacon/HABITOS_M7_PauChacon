package com.example.projecte_m07

import android.app.AlertDialog
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
import com.example.recyclerview.habitos.Habito
import com.example.recyclerview.habitos.HabitoCreate
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EditarHabitos : AppCompatActivity() {

    private lateinit var inputHabitName: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var switchImportance: Switch
    private lateinit var textSelectedTime: TextView
    private lateinit var buttonSelectTime: ImageButton
    private lateinit var buttonCreateHabit: AppCompatButton
    private lateinit var habitsContainer: LinearLayout

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
        loadLatestHabitsFromAPI()
        updateTimeDisplay()
    }

    private fun initializeViews() {
        inputHabitName = findViewById(R.id.inputHabitName)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        switchImportance = findViewById(R.id.switchImportance)
        textSelectedTime = findViewById(R.id.textSelectedTime)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)
        buttonCreateHabit = findViewById(R.id.buttonCreateHabit)
        habitsContainer = findViewById(R.id.listaHabitos)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<AppCompatButton>(R.id.buttonVolver).setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
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

            switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(thumbColor)
            )
            switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(trackColor)
            )
        }

        // Color inicial
        switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor("#9E9E9E")
        )
        switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor("#E0E0E0")
        )
    }

    private fun setupNavigation() {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageView>(R.id.buttonMenu)

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
                else -> false
            }.also {
                drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
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
                HabitosAPI.API().createHabito(newHabit)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitos, "Hábito '$habitName' creado correctamente", Toast.LENGTH_SHORT).show()
                    clearForm()
                    loadLatestHabitsFromAPI()
                    buttonCreateHabit.isEnabled = true
                    buttonCreateHabit.text = "CREAR HÁBITO"
                }
            } catch (e: Exception) {
                Log.e("EditarHabitos", "Error al crear hábito", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitos, "Error al crear hábito: ${e.message}", Toast.LENGTH_LONG).show()
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

        switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor("#9E9E9E")
        )
        switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor("#E0E0E0")
        )
    }

    private fun loadLatestHabitsFromAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allHabits = HabitosAPI.API().getHabitos()
                val latestHabits = allHabits.takeLast(3).reversed()

                withContext(Dispatchers.Main) {
                    displayLatestHabits(latestHabits)
                }
            } catch (e: Exception) {
                Log.e("EditarHabitos", "Error al cargar hábitos", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitos, "Error al cargar hábitos recientes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayLatestHabits(habits: List<Habito>) {
        habitsContainer.removeAllViews()
        habits.forEach { addHabitView(it) }
    }

    private fun addHabitView(habito: Habito) {
        val habitLayout = LinearLayout(this)
        habitLayout.orientation = LinearLayout.HORIZONTAL
        habitLayout.setPadding(32, 32, 32, 32)
        habitLayout.setBackgroundResource(R.drawable.habit_background)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 32)
        habitLayout.layoutParams = layoutParams

        val textView = TextView(this)
        val timeText = habito.hora?.let {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(it)
        } ?: "Sin hora"

        textView.text = "${habito.nombre}\n${habito.categoria} - $timeText"
        textView.textSize = 16f
        textView.setPadding(0, 0, 16, 0)
        textView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        textView.setTextColor(
            if (habito.importante) android.graphics.Color.parseColor("#FF0000")
            else android.graphics.Color.BLACK
        )

        val deleteButton = ImageButton(this)
        deleteButton.setImageResource(R.drawable.deletecross)
        deleteButton.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        deleteButton.layoutParams = LinearLayout.LayoutParams(64, 64)
        deleteButton.scaleType = ImageView.ScaleType.FIT_CENTER
        deleteButton.adjustViewBounds = true
        deleteButton.setPadding(8, 8, 8, 8)

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(habito, habitLayout)
        }

        habitLayout.addView(textView)
        habitLayout.addView(deleteButton)
        habitsContainer.addView(habitLayout)
    }

    private fun showDeleteConfirmationDialog(habito: Habito, habitLayout: LinearLayout) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el hábito '${habito.nombre}'?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                deleteHabitFromAPI(habito, habitLayout)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteHabitFromAPI(habito: Habito, habitLayout: LinearLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().deleteHabito(habito.id)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        habitsContainer.removeView(habitLayout)
                        Toast.makeText(this@EditarHabitos, "Hábito '${habito.nombre}' eliminado correctamente", Toast.LENGTH_SHORT).show()
                        loadLatestHabitsFromAPI()
                    } else {
                        Toast.makeText(this@EditarHabitos, "Error al eliminar el hábito", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("EditarHabitos", "Error al eliminar hábito", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitos, "Error al eliminar hábito: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        loadLatestHabitsFromAPI()
    }
}