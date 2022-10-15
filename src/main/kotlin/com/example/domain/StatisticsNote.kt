package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsNote(
    val startDateTimeAsString: String,
    val gamemodeTitle: String,
    val durationOfGameInSeconds: Double,
    val restartsCount: Int,
    val durationAsBeautyString: String
)
