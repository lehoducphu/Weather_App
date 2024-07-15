package com.example.weatherforecast.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectDbUtil {
    private String DB_PATH_SUFFIX = "/databases/";
    private SQLiteDatabase database = null;
    private String DATABASE_NAME = "WeatherForecast.db";
    private Context context; // Added context to use in Toast and getAssets

    // Constructor to receive context
    public ConnectDbUtil(Context context) {
        this.context = context;
    }

    public void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) { // Check if the database does NOT exist
            try {
                CopyDataBaseFromAsset();
//                Toast.makeText(context, "Copying success from Assets folder", Toast.LENGTH_LONG).show();
                Log.v("processCopy", "Copying success from Assets folder");
            } catch (Exception e) {
//                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                Log.v("processCopy", e.toString());
            }
        }
    }

    public File getDatabasePath(String dbName) {
        return context.getDatabasePath(dbName);
    }

    private String getDatabasePath() {
        return context.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput = context.getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();

            File f = new File(context.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists()) f.mkdir();

            OutputStream myOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to open the database
    public SQLiteDatabase openDatabase() {
        String dbPath = context.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
        if (database == null) {
            database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }
}