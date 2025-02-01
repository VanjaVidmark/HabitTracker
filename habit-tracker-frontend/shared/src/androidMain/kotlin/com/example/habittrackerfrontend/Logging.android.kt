package com.example.habittrackerfrontend

import android.util.Log

actual fun logMessage(tag: String, message: String) {
    Log.d(tag, message)
}