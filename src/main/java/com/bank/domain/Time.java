package com.bank.domain;

import java.time.LocalTime;

public class Time {
    private LocalTime startDate;
    private LocalTime endDate;
    private LocalTime currentDate;

    public Time(String currentDate, String startDate, String endDate) {
        this.startDate = LocalTime.parse(startDate);
        this.endDate = LocalTime.parse(endDate);
        this.currentDate = LocalTime.parse(currentDate);
    }

    public LocalTime getStartDate() {
        return startDate;
    }

    public LocalTime getEndDate() {
        return endDate;
    }

    public LocalTime getCurrentDate() {
        return currentDate;
    }
}
