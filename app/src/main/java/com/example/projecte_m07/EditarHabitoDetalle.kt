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

class EditarHabitoDetalle : AppCompatActivity() {

    private lateinit var inputHabitName: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var switchImportance: Switch
    private lateinit var textSelectedTime: TextView
    private lateinit var buttonSelectTime: ImageButton
    private lateinit var buttonUpdateHabit: AppCompatButton
    private lateinit var buttonDeleteHabit: AppCompatButton

    private var selectedHour: Int = 8
    private var selectedMinute: Int = 0
    private var habitoId: Int = -1

    private val categories = arrayOf("Salud", "Productividad", "Ocio", "Bienestar", "Hogar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_habito_detalle)

        initializeViews()
        setupSpinner()
        setupListeners()
        setupNavigation()
        loadHabitData()
    }

    private fun initializeViews() {
        inputHabitName = findViewById(R.id.inputHabitName)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        switchImportance = findViewById(R.id.switchImportance)
        textSelectedTime = findViewById(R.id.textSelectedTime)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)
        buttonUpdateHabit = findViewById(R.id.buttonUpdateHabit)
        buttonDeleteHabit = findViewById(R.id.buttonDeleteHabit)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupListeners() {
        val volverButton: AppCompatButton = findViewById(R.id.buttonVolver)
        volverButton.setOnClickListener {
            finish()
        }

        buttonSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        textSelectedTime.setOnClickListener {
            showTimePickerDialog()
        }

        buttonUpdateHabit.setOnClickListener {
            showUpdateConfirmationDialog()
        }

        buttonDeleteHabit.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        setupSwitchColors()
    }

    private fun setupSwitchColors() {
        switchImportance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#4CAF50")
                )
                switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#81C784")
                )
            } else {
                switchImportance.thumbTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#9E9E9E")
                )
                switchImportance.trackTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E0E0E0")
                )
            }
        }
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

    private fun loadHabitData() {
        // Obtener datos del intent
        habitoId = intent.getIntExtra("HABITO_ID", -1)
        val habitoNombre = intent.getStringExtra("HABITO_NOMBRE") ?: ""
        val habitoCategoria = intent.getStringExtra("HABITO_CATEGORIA") ?: "Salud"
        val habitoImportante = intent.getBooleanExtra("HABITO_IMPORTANTE", false)
        val habitoHora = intent.getStringExtra("HABITO_HORA") ?: "08:00"

        if (habitoId == -1) {
            Toast.makeText(this, "Error: No se pudo cargar el hábito", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos en los campos
        inputHabitName.setText(habitoNombre)

        // Seleccionar categoría en el spinner
        val categoryIndex = categories.indexOf(habitoCategoria)
        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(categoryIndex)
        }

        switchImportance.isChecked = habitoImportante

        // Parsear y cargar la hora
        try {
            val timeParts = habitoHora.split(":")
            selectedHour = timeParts[0].toInt()
            selectedMinute = timeParts[1].toInt()
            updateTimeDisplay()
        } catch (e: Exception) {
            selectedHour = 8
            selectedMinute = 0
            updateTimeDisplay()
        }
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                updateTimeDisplay()
            },
            selectedHour,
            selectedMinute,
            true
        )
        timePickerDialog.show()
    }

    private fun updateTimeDisplay() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        textSelectedTime.text = timeFormat.format(calendar.time)
    }

    private fun showUpdateConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Edición")
            .setMessage("¿Estás seguro de que quieres editar este hábito?")
            .setPositiveButton("Sí, editar") { _, _ ->
                updateHabit()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar este hábito? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                deleteHabit()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateHabit() {
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

        buttonUpdateHabit.isEnabled = false
        buttonUpdateHabit.text = "Actualizando..."

        val habitoActualizado = HabitoCreate(
            nombre = habitName,
            categoria = selectedCategory,
            importante = isImportant,
            hora = timeString
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitoUpdated = HabitosAPI.API().updateHabito(habitoId, habitoActualizado)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitoDetalle, "Hábito '${habitoUpdated.nombre}' actualizado correctamente", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@EditarHabitoDetalle, Menu::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                Log.e("EditarHabitoDetalle", "Error al actualizar hábito", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitoDetalle, "Error al actualizar hábito: ${e.message}", Toast.LENGTH_LONG).show()
                    buttonUpdateHabit.isEnabled = true
                    buttonUpdateHabit.text = "ACTUALIZAR HÁBITO"
                }
            }
        }
    }

    private fun deleteHabit() {
        buttonDeleteHabit.isEnabled = false
        buttonDeleteHabit.text = "Eliminando..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HabitosAPI.API().deleteHabito(habitoId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarHabitoDetalle, "Hábito eliminado correctamente", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@EditarHabitoDetalle, Menu::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@EditarHabitoDetalle, "Error al eliminar hábito", Toast.LENGTH_SHORT).show()
                        buttonDeleteHabit.isEnabled = true
                        buttonDeleteHabit.text = "ELIMINAR HÁBITO"
                    }
                }
            } catch (e: Exception) {
                Log.e("EditarHabitoDetalle", "Error al eliminar hábito", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarHabitoDetalle, "Error al eliminar hábito: ${e.message}", Toast.LENGTH_LONG).show()
                    buttonDeleteHabit.isEnabled = true
                    buttonDeleteHabit.text = "ELIMINAR HÁBITO"
                }
            }
        }
    }
}