package com.example.lodowka;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmHelper {

    /**
     * Ustawia codzienny alarm na godzinę 9:00 rano.
     */
    public static void setDailyAlarm(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        // Ustawienie na 9:00 rano
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Jeśli 9:00 już minęła dzisiaj, ustaw na jutro
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return "Następne powiadomienie: " + sdf.format(calendar.getTime());
    }
}
