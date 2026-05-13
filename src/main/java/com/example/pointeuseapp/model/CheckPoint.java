package com.example.pointeuseapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CheckPoint implements Serializable {

    private String employeeId;
    private LocalDateTime dateTime;

    public CheckPoint(String employeeId, LocalDateTime dateTime) {
        this.employeeId = employeeId;
        this.dateTime = roundToQuarter(dateTime);
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    private LocalDateTime roundToQuarter(LocalDateTime time) {
        int minutes = time.getMinute();

        int rounded = ((minutes + 7) / 15) * 15;

        return time.withMinute(0).plusMinutes(rounded);
    }
}
