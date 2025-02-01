package com.example.habittrackerfrontend

import android.content.res.Resources
import android.os.Build
import android.util.Log
import kotlin.math.round

actual object Platform {
    actual val osName: String
        get() = "Android"

    actual val osVersion: String
        get() = "${Build.VERSION.SDK_INT}"

    actual val deviceModel: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"

    actual val density: Int
        get() = round(Resources.getSystem().displayMetrics.density).toInt()

    actual fun getBaseUrl(): String = "http://10.0.2.2:8080"

    actual fun logSystemInfo() {
        Log.d(
            "Daily Pulse",
            "($osName, $osVersion, $deviceModel, $density)"
        )
    }
}