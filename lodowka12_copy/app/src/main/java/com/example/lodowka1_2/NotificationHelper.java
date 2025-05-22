package com.example.lodowka1_2;

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
        Calendar now = Calendar.getInstance();

        for (FoodItem item : foodList) {
            long diff = (item.getExpiryDate().getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60 * 60 * 24);
            if (diff <= 3) {
                message.append("- ").append(item.getName())
                        .append(" (").append(sdf.format(item.getExpiryDate().getTime())).append(")\n");
            }
        }

        String finalMessage = message.length() > 0
                ? "Produkty z krótkim terminem ważności:\n" + message
                : "Brak produktów z terminem ważności za 3 dni lub mniej.";

        showNotification(context, "Przypomnienie z lodówki", finalMessage);
    }

    private static void showNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Powiadomienia o ważności", NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.lodowka)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
