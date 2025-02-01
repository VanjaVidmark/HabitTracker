package com.example.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
data class Entry(
    val id: String = UUID.randomUUID().toString(),
    var note: String? = null,  // optional
    var timestamp: String
)

