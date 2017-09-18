package com.bank.service.internal

import com.bank.domain.Time

open class TimePolicy constructor(private val time: Time) {

    fun isTimeValid(): Boolean {
        val isAfter = time.currentTime.isAfter(time.startTime)
        val isBefore = time.currentTime.isBefore(time.endTime)
        return isAfter && isBefore
    }
}