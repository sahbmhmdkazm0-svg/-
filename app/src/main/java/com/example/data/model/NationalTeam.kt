package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "national_teams")
data class NationalTeam(
    @PrimaryKey
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val continentAr: String,
    val continentEn: String,
    val flagEmoji: String,
    val ratingStars: Int = 4,
    val isAvailable: Boolean = true
)
