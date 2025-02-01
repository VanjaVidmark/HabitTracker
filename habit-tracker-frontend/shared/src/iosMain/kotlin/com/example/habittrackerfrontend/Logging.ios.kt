package com.example.habittrackerfrontend

actual fun logMessage(tag: String, message: String) {
    println("$tag: $message")
}
