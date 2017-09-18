package com.bank.service.internal;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class TimePolicyTest {

    @Test
    @Parameters({
            "06:00",
            "12:00",
            "15:00",
            "18:00",
            "20:00",
            "21:59"
    })
    public void current_time_should_valid_policy(String currentTime) {
        TimePolicy timePolicy = new TimePolicy("05:59", "22:00");
        boolean actual = timePolicy.isTimeValid(currentTime);
        assertTrue(actual);
    }

    @Test
    @Parameters({
            "06:00 | 05:00 | 21:00",
            "10:01 | 10:00 | 21:00"
    })
    public void change_start_and_end_time_should_valid_polity(String currentTime,
                                                              String startTime, String endTime) throws Exception {
        TimePolicy timePolicy = new TimePolicy(startTime, endTime);
        boolean actual = timePolicy.isTimeValid(currentTime);
        assertTrue(actual);
    }
}