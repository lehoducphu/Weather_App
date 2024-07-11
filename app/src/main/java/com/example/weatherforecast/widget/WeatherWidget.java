package com.example.weatherforecast.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.weatherforecast.R;
import com.example.weatherforecast.view.MainViewActivity;

public class WeatherWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainViewActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, -1);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

            // Update the widget UI elements here
            views.setTextViewText(R.id.widget_temp, "27°C");
            views.setTextViewText(R.id.widget_conditions, "Sunny");

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
