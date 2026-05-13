package com.example.pointeuseapp.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalDateTime roundToNearestQuarter(LocalDateTime time) {
        int minutes = time.getMinute();
        int roundedMinutes = ((minutes + 7) / 15) * 15;

        if (roundedMinutes == 60) {
            return time.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
        return time.withMinute(roundedMinutes).withSecond(0).withNano(0);
    }
}