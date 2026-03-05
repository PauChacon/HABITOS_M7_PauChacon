package com.example.recyclerview.habitos

import java.text.SimpleDateFormat
import java.util.*

fun String.toHourMinuteDate(format: String = "HH:mm"): Date? {
    val formatter = SimpleDateFormat(format, Locale.US)
    return formatter.parse(this)
}

fun Date.toHourMinuteString(format: String = "HH:mm"): String {
    val formatter = SimpleDateFormat(format, Locale.US)
    return formatter.format(this)
}
