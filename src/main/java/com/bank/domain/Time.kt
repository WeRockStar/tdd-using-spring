package com.bank.domain

import java.time.LocalTime

class Time(currentTime: String, startTime: String, endTime: String) {
    val startTime: LocalTime = LocalTime.parse(startTime)
    val endTime: LocalTime = LocalTime.parse(endTime)
    val currentTime: LocalTime = LocalTime.parse(currentTime)
}
