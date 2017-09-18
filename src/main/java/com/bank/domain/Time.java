package com.bank.domain;

import java.time.LocalTime;

public class Time {
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime currentTime;

    public Time(String currentTime, String startTime, String endTime) {
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
        this.currentTime = LocalTime.parse(currentTime);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }
}
