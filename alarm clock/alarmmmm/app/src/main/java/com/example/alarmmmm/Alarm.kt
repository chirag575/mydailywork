package com.example.alarmmmm

import java.time.LocalTime

data class Alarm(
    val id: Int,
    val time: LocalTime,
    val isEnabled: Boolean = true,
    val label: String = "Alarm"
)
