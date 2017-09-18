package com.bank.service.internal

import com.bank.domain.Time
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.assertTrue

@RunWith(JUnitParamsRunner::class)
class TimePolicyTest {

    @Test
    @Parameters("06:00", "12:00", "15:00", "18:00", "20:00", "21:59")
    fun current_time_should_valid_policy(currentTime: String) {
        val time = Time(currentTime, "05:59", "22:00")
        val timePolicy = TimePolicy(time)
        val actual = timePolicy.isTimeValid()
        assertTrue(actual)
    }

    @Test
    @Parameters("06:00 | 05:00 | 21:00", "10:01 | 10:00 | 21:00")
    fun change_start_and_end_time_should_valid_polity(currentTime: String,
                                                      startTime: String, endTime: String) {
        val time = Time(currentTime, startTime, endTime)
        val timePolicy = TimePolicy(time)
        val actual = timePolicy.isTimeValid()
        assertTrue(actual)
    }
}