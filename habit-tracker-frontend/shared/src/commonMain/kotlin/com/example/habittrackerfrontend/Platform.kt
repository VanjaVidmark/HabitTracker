package com.example.habittrackerfrontend

expect object Platform {
    val osName: String
    val osVersion: String
    val deviceModel: String
    val density: Int

    fun getBaseUrl(): String
    fun logSystemInfo()
}
