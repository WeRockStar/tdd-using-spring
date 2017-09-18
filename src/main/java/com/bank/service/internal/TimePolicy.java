package com.bank.service.internal;

import com.bank.domain.Time;

public class TimePolicy {
    private Time time;

    public TimePolicy(Time time) {
        this.time = time;
    }

    public boolean isTimeValid() {
        boolean isAfter = time.getCurrentDate().isAfter(time.getStartDate());
        boolean isBefore = time.getCurrentDate().isBefore(time.getEndDate());
        return isAfter && isBefore;
    }
}
