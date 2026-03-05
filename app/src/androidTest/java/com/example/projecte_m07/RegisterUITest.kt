package com.example.projecte_m07

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent


import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(Register::class.java)

    @Test fun usernameTooShort() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("ab"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("El nombre de usuario debe tener más de 3 caracteres")).check(matches(isDisplayed()))
    }

    @Test fun usernameEmpty() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("El nombre de usuario debe tener más de 3 caracteres")).check(matches(isDisplayed()))
    }

    @Test fun invalidEmailNoAt() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("testgmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("Introduce un correo electrónico válido")).check(matches(isDisplayed()))
    }

    @Test fun invalidEmailNoDomain() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("Introduce un correo electrónico válido")).check(matches(isDisplayed()))
    }

    @Test fun invalidPhoneShort() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("12345"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("El número de teléfono debe tener exactamente 9 cifras")).check(matches(isDisplayed()))
    }

    @Test fun invalidPhoneLetters() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("abcdefghi"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("El número de teléfono debe tener exactamente 9 cifras")).check(matches(isDisplayed()))
    }

    @Test fun weakPasswordNoSymbol() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo")).check(matches(isDisplayed()))
    }

    @Test fun passwordMismatch() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("Las contraseñas no coinciden")).check(matches(isDisplayed()))
    }
/**
    @Test
    fun allValidFields() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuarioValido"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("correo@valido.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        intended(hasComponent(Menu::class.java.name))
    }

    **/
    @Test
    fun usernameTooLong() {
        val longUsername = "a".repeat(100)
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText(longUsername), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withId(R.id.textViewRegisterError)).check(matches(isDisplayed()))

        onView(withId(R.id.textViewRegisterError)).check(matches(withText("El nombre de usuario debe tener menos de 25 caracteres")))
    }



    @Test fun emailEmpty() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("Introduce un correo electrónico válido")).check(matches(isDisplayed()))
    }

    @Test fun phoneEmpty() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("El número de teléfono debe tener exactamente 9 cifras")).check(matches(isDisplayed()))
    }

    @Test fun passwordEmpty() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo")).check(matches(isDisplayed()))
    }

    @Test fun confirmPasswordEmpty() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("Las contraseñas no coinciden")).check(matches(isDisplayed()))
    }

    @Test fun phoneTooLong() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("1234567890"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("El número de teléfono debe tener exactamente 9 cifras")).check(matches(isDisplayed()))
    }

    @Test fun passwordNoUppercase() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("abcd@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo")).check(matches(isDisplayed()))
    }

    @Test fun passwordNoLowercase() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("ABCD@1234"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("ABCD@1234"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo")).check(matches(isDisplayed()))
    }

    @Test fun passwordNoNumber() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Abcd@abcd"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Abcd@abcd"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo")).check(matches(isDisplayed()))
    }

    @Test fun passwordTooShort() {
        onView(withId(R.id.editTextRegisterUsername)).perform(typeText("usuario"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterEmail)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPhone)).perform(typeText("612345678"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterPassword)).perform(typeText("Ab@1"), closeSoftKeyboard())
        onView(withId(R.id.editTextRegisterConfirmPassword)).perform(typeText("Ab@1"), closeSoftKeyboard())
        onView(withId(R.id.buttonConfirmRegister)).perform(click())

        onView(withText("La contraseña debe tener más de 8 caracteres, mayúscula, minúscula, número y símbolo")).check(matches(isDisplayed()))
    }
}

