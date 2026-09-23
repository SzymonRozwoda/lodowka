package com.example.lodowka;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NotificationHelper {

    private static final String CHANNEL_ID = "expiry_channel";

    public static void sendDailyNotification(Context context, ArrayList<FoodItem> foodList) {
        StringBuilder message = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (FoodItem item : foodList) {
            Calendar expiry = (Calendar) item.getExpiryDate().clone();
            expiry.set(Calendar.HOUR_OF_DAY, 0);
            expiry.set(Calendar.MINUTE, 0);
            expiry.set(Calendar.SECOND, 0);
            expiry.set(Calendar.MILLISECOND, 0);

            long diffDays = (expiry.getTimeInMillis() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24);
            if (diffDays <= item.getReminderDaysBefore()) {
                message.append("- ").append(item.getName())
                        .append(" (").append(sdf.format(item.getExpiryDate().getTime())).append(")\n");
            }
        }

        if (message.length() == 0) {
            if (AlarmHelper.isOnlyNearExpiryEnabled(context)) {
                return; // Użytkownik chce powiadomienia tylko gdy produkty są blisko terminu
            }
            showNotification(context, "Przypomnienie z lodówki", "Brak produktów z kończącym się terminem ważności.");
        } else {
            showNotification(context, "Przypomnienie z lodówki", "Produkty z krótkim terminem ważności:\n" + message);
        }
    }

    private static void showNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Powiadomienia o ważności", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Powiadomienia o kończącej się dacie ważności produktów.");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
