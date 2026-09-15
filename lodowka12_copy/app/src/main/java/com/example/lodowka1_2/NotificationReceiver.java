package com.example.lodowka1_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "!!! ALARM ODEBRANY !!! - Godzina: " + new java.util.Date());
        ArrayList<FoodItem> foodList = StorageHelper.loadFoodItems(context, "foods.txt");
        Log.d("NotificationReceiver", "Załadowano " + foodList.size() + " produktów.");
        NotificationHelper.sendDailyNotification(context, foodList);
        
        // Ponowne ustawienie alarmu na kolejny dzień
        AlarmHelper.setDailyAlarm(context);
    }
}
