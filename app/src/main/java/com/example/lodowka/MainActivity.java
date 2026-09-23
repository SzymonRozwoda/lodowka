package com.example.lodowka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<FoodItem> foodList;
    private FoodAdapter adapter;
    private RecyclerView recyclerView;
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Elementy menu FAB
    private FloatingActionButton fabMain, fabAddItem, fabSettings;
    private LinearLayout fabMenuContainer;
    private View fabMenuOverlay;
    private boolean isMenuOpen = false;

    private static final String PREFS_NAME = "lodowka_prefs";
    private static final String KEY_THEME = "app_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inicjalizacja motywu przed super.onCreate
        applySavedTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayCurrentDate();

        foodList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewFoodItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        StorageHelper.migrateLegacyDataIfNeeded(this);
        AppDatabase.getInstance(this).foodDao().getAllFoodItemsLiveData().observe(this, items -> {
            foodList.clear();
            if (items != null) {
                foodList.addAll(items);
            }
            adapter.notifyDataSetChanged();
        });

        setupFabMenu();
        setupSwipeToDelete();

        // Podstawowe sprawdzenie uprawnień i ustawień
        checkPermissions();
        checkBatteryOptimizations();

        // Ustawienie alarmu (wczytuje zapisaną godzinę lub domyślną 9:00)
        AlarmHelper.setDailyAlarm(this);
    }

    private void displayCurrentDate() {
        TextView textCurrentDate = findViewById(R.id.textCurrentDate);
        if (textCurrentDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            textCurrentDate.setText(sdf.format(new Date()));
        }
    }

    private void setupFabMenu() {
        fabMain = findViewById(R.id.fab_main);
        fabAddItem = findViewById(R.id.fab_add_item);
        fabSettings = findViewById(R.id.fab_settings);
        fabMenuContainer = findViewById(R.id.fab_menu_container);
        fabMenuOverlay = findViewById(R.id.fab_menu_overlay);

        fabMain.setOnClickListener(v -> toggleFabMenu());
        if (fabMenuOverlay != null) {
            fabMenuOverlay.setOnClickListener(v -> toggleFabMenu());
        }

        fabAddItem.setOnClickListener(v -> {
            toggleFabMenu();
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        fabSettings.setOnClickListener(v -> {
            toggleFabMenu();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    private void toggleFabMenu() {
        if (!isMenuOpen) {
            fabMenuContainer.setVisibility(View.VISIBLE);
            if (fabMenuOverlay != null) fabMenuOverlay.setVisibility(View.VISIBLE);
            fabMain.animate().rotation(45f).setDuration(200).start();
            isMenuOpen = true;
        } else {
            fabMenuContainer.setVisibility(View.GONE);
            if (fabMenuOverlay != null) fabMenuOverlay.setVisibility(View.GONE);
            fabMain.animate().rotation(0f).setDuration(200).start();
            isMenuOpen = false;
        }
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                FoodItem deletedItem = foodList.get(position);
                StorageHelper.deleteFoodItem(MainActivity.this, deletedItem);
                Toast.makeText(MainActivity.this, "Usunięto: " + deletedItem.getName(), Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private void checkBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
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
    }
}
