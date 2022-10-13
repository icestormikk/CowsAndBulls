package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsNote(
    val startDateTimeAsString: String,
    val gamemodeTitle: String,
    val durationOfGameAsString: String,
    val restartsCount: Int
)
