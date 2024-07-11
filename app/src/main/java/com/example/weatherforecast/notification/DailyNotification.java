package com.example.weatherforecast.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.example.weatherforecast.R;

public class DailyNotification {
    private Context context;

    public DailyNotification(Context context) {
        this.context = context;
    }
    private void createNotificationChannel() {
        CharSequence name = context.getString(R.string.channel_name); // You need to add this string to your strings.xml
        String description = context.getString(R.string.channel_description); // You need to add this string to your strings.xml
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("weatherChannelId", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
