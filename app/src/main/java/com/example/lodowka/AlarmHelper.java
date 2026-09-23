package com.example.lodowka;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmHelper {

    private static final String PREFS_NAME = "lodowka_prefs";
    private static final String KEY_HOUR = "alarm_hour";
    private static final String KEY_MINUTE = "alarm_minute";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_ONLY_NEAR_EXPIRY = "only_near_expiry";

    /**
     * Ustawia codzienny alarm na godzinę zapisaną w ustawieniach (domyślnie 9:00).
     */
    public static void setDailyAlarm(Context context) {
        if (!isNotificationsEnabled(context)) {
            cancelAlarm(context);
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int hour = prefs.getInt(KEY_HOUR, 9);
        int minute = prefs.getInt(KEY_MINUTE, 0);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        // Ustawienie na wybraną godzinę
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Jeśli czas już minął dzisiaj, ustaw na jutro
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Log.e("AlarmHelper", "Brak uprawnień do dokładnych alarmów (API 31+).");
                        return;
                    }
                }
                
                // Użycie setAlarmClock dla maksymalnej niezawodności
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                Log.d("AlarmHelper", "Alarm (setAlarmClock) ustawiony na: " + calendar.getTime());
                
            } catch (Exception e) {
                Log.e("AlarmHelper", "Błąd podczas ustawiania alarmu: " + e.getMessage());
            }
        }
    }

    public static String getNextAlarmStatus(Context context) {
        if (!isNotificationsEnabled(context)) {
            return "Powiadomienia: WYŁĄCZONE";
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int hour = prefs.getInt(KEY_HOUR, 9);
        int minute = prefs.getInt(KEY_MINUTE, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return "Następne powiadomienie: " + sdf.format(calendar.getTime());
    }

    public static void saveAlarmTime(Context context, int hour, int minute) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_HOUR, hour)
                .putInt(KEY_MINUTE, minute)
                .apply();
    }

    public static boolean isNotificationsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public static void setNotificationsEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
        if (enabled) {
            setDailyAlarm(context);
        } else {
            cancelAlarm(context);
        }
    }

    public static boolean isOnlyNearExpiryEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ONLY_NEAR_EXPIRY, false);
    }

    public static void setOnlyNearExpiryEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ONLY_NEAR_EXPIRY, enabled).apply();
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("AlarmHelper", "Alarm anulowany.");
        }
    }
}
