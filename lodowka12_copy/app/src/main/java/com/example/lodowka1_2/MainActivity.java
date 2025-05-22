package com.example.lodowka1_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<FoodItem> foodList;
    private ArrayAdapter<FoodItem> adapter;
    private ListView listView;
    private static final String FILE_NAME = "foods.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodList = StorageHelper.loadFoodItems(this, FILE_NAME);

        listView = findViewById(R.id.listViewFoodItems);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodList);
        listView.setAdapter(adapter);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        Button testNotificationBtn = findViewById(R.id.testNotificationBtn);
        testNotificationBtn.setOnClickListener(v -> {
            NotificationHelper.sendDailyNotification(this, foodList);
        });

        setDailyAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        foodList.clear();
        foodList.addAll(StorageHelper.loadFoodItems(this, FILE_NAME));
        adapter.notifyDataSetChanged();
    }

    private void setDailyAlarm() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }
}
