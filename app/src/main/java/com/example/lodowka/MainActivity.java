package com.example.lodowka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<FoodItem> foodList;
    private ArrayAdapter<FoodItem> adapter;
    private ListView listView;
    private TextView alarmStatusText;
    private static final String FILE_NAME = "foods.txt";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Elementy menu FAB
    private FloatingActionButton fabMain, fabAddItem, fabSettings;
    private LinearLayout fabMenuContainer;
    private boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodList = StorageHelper.loadFoodItems(this, FILE_NAME);

        listView = findViewById(R.id.listViewFoodItems);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodList);
        listView.setAdapter(adapter);

        alarmStatusText = findViewById(R.id.alarmStatusText);

        setupFabMenu();

        // Podstawowe sprawdzenie uprawnień i ustawień
        checkPermissions();
        checkBatteryOptimizations();

        // Ustawienie alarmu na 9:00
        AlarmHelper.setDailyAlarm(this);
        updateAlarmStatus();
    }

    private void setupFabMenu() {
        fabMain = findViewById(R.id.fab_main);
        fabAddItem = findViewById(R.id.fab_add_item);
        fabSettings = findViewById(R.id.fab_settings);
        fabMenuContainer = findViewById(R.id.fab_menu_container);

        fabMain.setOnClickListener(v -> toggleFabMenu());

        fabAddItem.setOnClickListener(v -> {
            toggleFabMenu(); // Zamknij menu
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        fabSettings.setOnClickListener(v -> {
            toggleFabMenu(); // Zamknij menu
            Toast.makeText(this, "Ustawienia wkrótce!", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleFabMenu() {
        if (!isMenuOpen) {
            // Otwieranie
            fabMenuContainer.setVisibility(View.VISIBLE);
            fabMain.animate().rotation(45f).setDuration(200).start();
            isMenuOpen = true;
        } else {
            // Zamykanie
            fabMenuContainer.setVisibility(View.GONE);
            fabMain.animate().rotation(0f).setDuration(200).start();
            isMenuOpen = false;
        }
    }

    private void updateAlarmStatus() {
        if (alarmStatusText != null) {
            alarmStatusText.setText(AlarmHelper.getNextAlarmStatus(this));
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Wymagana zgoda na dokładne alarmy.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private void checkBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Toast.makeText(this, "Wyłącz optymalizację baterii dla powiadomień rano.", Toast.LENGTH_LONG).show();
            @SuppressLint("BatteryLife")
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Uprawnienia przyznane", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        foodList.clear();
        foodList.addAll(StorageHelper.loadFoodItems(this, FILE_NAME));
        adapter.notifyDataSetChanged();
        updateAlarmStatus();
    }
}
