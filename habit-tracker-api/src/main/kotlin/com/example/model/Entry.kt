package com.example.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Entry(
    val id: String = UUID.randomUUID().toString(),
    var note: String? = null,  // optional
    var timestamp: LocalDate
)

