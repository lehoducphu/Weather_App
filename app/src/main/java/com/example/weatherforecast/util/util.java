package com.example.weatherforecast.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.example.weatherforecast.model.dbmodel.DbCity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public static List<DbCity> getUserSavedcities(Context context) {
        // Get the list of cities from the database
        List<DbCity> dbCityList = new ArrayList<>();
        ConnectDbUtil dbUtil = new ConnectDbUtil(context); // 'this' is the Activity context
        dbUtil.processCopy();
        SQLiteDatabase database = dbUtil.openDatabase();

        // query data
        Cursor c = database.rawQuery(
                "SELECT id, city_name, city_longitude, city_latitude, country, state " +
                        "FROM city " +
                        "where id IN " +
                        "(SELECT city_id FROM users_city WHERE users_id = 1)", null);

        c.moveToFirst();

        // get data
        while (c.isAfterLast() == false) {
            DbCity dbCity = new DbCity(c.getInt(0), c.getString(1),
                    c.getDouble(2), c.getDouble(3),
                    c.getString(4), c.getString(5));

            dbCityList.add(dbCity);
            c.moveToNext();
        }
        c.close();
        return dbCityList;
    }

    public static void deleteUserCityById(Context context, int cityId) {
        ConnectDbUtil dbUtil = new ConnectDbUtil(context);
        dbUtil.processCopy();
        SQLiteDatabase database = dbUtil.openDatabase();

        // Assuming 'users_city' is the table name and 'city_id' is the column name
        String whereClause = "city_id=?";
        String[] whereArgs = new String[] { String.valueOf(cityId) };

        database.delete("users_city", whereClause, whereArgs);

        database.close();
    }

    public static void addCity(Context context, String cityName, double cityLongitude, double cityLatitude, String country, String state) {
        ConnectDbUtil dbUtil = new ConnectDbUtil(context);
        dbUtil.processCopy();
        SQLiteDatabase database = dbUtil.openDatabase();

        // Prepare the values for the city table
        ContentValues cityValues = new ContentValues();
        cityValues.put("city_name", cityName);
        cityValues.put("city_longitude", cityLongitude);
        cityValues.put("city_latitude", cityLatitude);
        cityValues.put("country", country);
        cityValues.put("state", state);

        // Insert the new city into the city table
        long cityId = database.insert("city", null, cityValues);

        // Prepare the values for the users_city table
        ContentValues usersCityValues = new ContentValues();
        usersCityValues.put("city_id", cityId);
        usersCityValues.put("users_id", 1); // Assuming the user's ID is always 1 for this example

        // Insert the new record into the users_city table
        database.insert("users_city", null, usersCityValues);

        database.close();
    }
}
