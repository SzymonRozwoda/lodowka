package com.example.lodowka1_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<FoodItem> foodList = StorageHelper.loadFoodItems(context, "foods.txt");
        NotificationHelper.sendDailyNotification(context, foodList);
    }
}
