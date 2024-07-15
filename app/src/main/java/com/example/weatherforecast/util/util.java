package com.example.weatherforecast.util;

import android.os.Build;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class util {
    public static LocalDateTime convertUnixToLocalDateTime(long unixSeconds, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), zoneId);

    }
}
