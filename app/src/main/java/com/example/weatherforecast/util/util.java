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

    // Wind Speed Converter
    public static double kmhToMph(double kmh) {
        return kmh * 0.621371;
    }

    public static double kmhToMs(double kmh) {
        return kmh / 3.6;
    }

    // Pressure Converter

    public static double mbToBar(double mb) {
        return mb * 0.001;
    }

    public static double mbToPsi(double mb) {
        return mb * 0.0145038;
    }

    public static double mbToInHg(double mb) {
        return mb * 0.02953;
    }
}
