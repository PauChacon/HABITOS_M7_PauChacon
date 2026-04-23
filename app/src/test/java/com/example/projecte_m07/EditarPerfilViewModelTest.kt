package com.example.projecte_m07

import com.example.projecte_m07.viewmodel.EditarPerfilViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EditarPerfilViewModelTest {

    private lateinit var viewModel: EditarPerfilViewModel

    @Before
    fun setup() {
        viewModel = EditarPerfilViewModel()
    }

    @Test
    fun usuarioCorrecto() {
        val result = viewModel.validate("usuario", "mail@gmail.com", "612345678")
        assertTrue(result)
    }

    @Test
    fun usuarioDemasiadoCorto() {
        val result = viewModel.validate("ab", "mail@gmail.com", "612345678")
        assertFalse(result)
    }

    @Test
    fun usuarioDemasiadoLargo() {
        val result = viewModel.validate("a".repeat(26), "mail@gmail.com", "612345678")
        assertFalse(result)
    }

    @Test
    fun emailSinArroba() {
        val result = viewModel.validate("usuario", "mailgmail.com", "612345678")
        assertFalse(result)
    }

    @Test
    fun emailSinDominio() {
        val result = viewModel.validate("usuario", "mail@", "612345678")
        assertFalse(result)
    }

    @Test
    fun emailVacio() {
        val result = viewModel.validate("usuario", "", "612345678")
        assertFalse(result)
    }

    @Test
    fun telefonoCorto() {
        val result = viewModel.validate("usuario", "mail@gmail.com", "12345")
        assertFalse(result)
    }

    @Test
    fun telefonoLargo() {
        val result = viewModel.validate("usuario", "mail@gmail.com", "1234567890")
        assertFalse(result)
    }

    @Test
    fun telefonoConLetras() {
        val result = viewModel.validate("usuario", "mail@gmail.com", "61234abc9")
        assertFalse(result)
    }

    @Test
    fun telefonoVacio() {
        val result = viewModel.validate("usuario", "mail@gmail.com", "")
        assertFalse(result)
    }

    @Test
    fun usuarioVacio() {
        val result = viewModel.validate("", "mail@gmail.com", "612345678")
        assertFalse(result)
    }
}