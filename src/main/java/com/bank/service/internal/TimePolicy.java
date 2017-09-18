package com.bank.service.internal;

import java.time.LocalTime;

public class TimePolicy {
    private LocalTime startTime;
    private LocalTime endTime;

    public TimePolicy(String startTime, String endTime) {
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
    }

    public boolean isTimeValid(String current) {
        LocalTime currentTime = LocalTime.parse(current);
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }
}
