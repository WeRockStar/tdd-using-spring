package com.bank.service.internal;

import com.bank.domain.Time;

public class TimePolicy {
    private Time time;

    public TimePolicy(Time time) {
        this.time = time;
    }

    public boolean isTimeValid() {
        boolean isAfter = time.getCurrentTime().isAfter(time.getStartTime());
        boolean isBefore = time.getCurrentTime().isBefore(time.getEndTime());
        return isAfter && isBefore;
    }
}
