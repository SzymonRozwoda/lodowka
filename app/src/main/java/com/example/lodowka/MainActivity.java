package com.example.lodowka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<FoodItem> foodList;
    private FoodAdapter adapter;
    private RecyclerView recyclerView;
    private TextView alarmStatusText;
    private static final String FILE_NAME = "foods.txt";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Elementy menu FAB
    private FloatingActionButton fabMain, fabAddItem, fabSettings, fabSetTime, fabTheme, fabToggleNotifications;
    private TextView textToggleNotifications;
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

        foodList = StorageHelper.loadFoodItems(this, FILE_NAME);

        recyclerView = findViewById(R.id.recyclerViewFoodItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        alarmStatusText = findViewById(R.id.alarmStatusText);

        setupFabMenu();
        setupSwipeToDelete();

        // Podstawowe sprawdzenie uprawnień i ustawień
        checkPermissions();
        checkBatteryOptimizations();

        // Ustawienie alarmu (wczytuje zapisaną godzinę lub domyślną 9:00)
        AlarmHelper.setDailyAlarm(this);
        updateAlarmStatus();
    }

    private void setupFabMenu() {
        fabMain = findViewById(R.id.fab_main);
        fabAddItem = findViewById(R.id.fab_add_item);
        fabSettings = findViewById(R.id.fab_settings);
        fabSetTime = findViewById(R.id.fab_set_time);
        fabTheme = findViewById(R.id.fab_theme);
        fabToggleNotifications = findViewById(R.id.fab_toggle_notifications);
        textToggleNotifications = findViewById(R.id.text_toggle_notifications);
        fabMenuContainer = findViewById(R.id.fab_menu_container);
        fabMenuOverlay = findViewById(R.id.fab_menu_overlay);

        fabMain.setOnClickListener(v -> toggleFabMenu());
        if (fabMenuOverlay != null) {
            fabMenuOverlay.setOnClickListener(v -> toggleFabMenu());
        }

        fabToggleNotifications.setOnClickListener(v -> {
            boolean isEnabled = AlarmHelper.isNotificationsEnabled(this);
            AlarmHelper.setNotificationsEnabled(this, !isEnabled);
            updateAlarmStatus();
            toggleFabMenu();
            String msg = !isEnabled ? "Powiadomienia włączone" : "Powiadomienia wyłączone";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        fabAddItem.setOnClickListener(v -> {
            toggleFabMenu(); // Zamknij menu
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        fabSettings.setOnClickListener(v -> {
            toggleFabMenu(); // Zamknij menu
            Toast.makeText(this, "Ustawienia wkrótce!", Toast.LENGTH_SHORT).show();
        });

        fabSetTime.setOnClickListener(v -> {
            toggleFabMenu(); // Zamknij menu
            showTimePickerDialog();
        });

        fabTheme.setOnClickListener(v -> {
            toggleFabMenu(); // Zamknij menu
            showThemeSelectionDialog();
        });
    }

    private void applySavedTheme() {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    private void showThemeSelectionDialog() {
        String[] themes = {"Jasny", "Ciemny", "Systemowy"};
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentTheme = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        int checkedItem = 2; // Systemowy
        if (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) checkedItem = 0;
        else if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) checkedItem = 1;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Wybierz motyw")
                .setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
                    int mode;
                    switch (which) {
                        case 0: mode = AppCompatDelegate.MODE_NIGHT_NO; break;
                        case 1: mode = AppCompatDelegate.MODE_NIGHT_YES; break;
                        default: mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM; break;
                    }
                    prefs.edit().putInt(KEY_THEME, mode).apply();
                    AppCompatDelegate.setDefaultNightMode(mode);
                    dialog.dismiss();
                })
                .show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Użycie motywu Holo Light Dialog wymusza klasyczny wygląd "rolek" (spinnerów)
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, hourOfDay, minute) -> {
            // Zapisanie nowej godziny
            AlarmHelper.saveAlarmTime(this, hourOfDay, minute);
            // Ponowne ustawienie alarmu
            AlarmHelper.setDailyAlarm(this);
            // Aktualizacja tekstu w menu
            updateAlarmStatus();
            
            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            Toast.makeText(this, "Powiadomienia ustawione na " + timeFormatted, Toast.LENGTH_LONG).show();
        }, currentHour, currentMinute, true);

        // Dodatkowe ustawienie tła na przezroczyste, aby okno wyglądało estetycznie w starym stylu
        if (timePickerDialog.getWindow() != null) {
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        timePickerDialog.show();
    }

    private void toggleFabMenu() {
        if (!isMenuOpen) {
            // Otwieranie
            fabMenuContainer.setVisibility(View.VISIBLE);
            if (fabMenuOverlay != null) fabMenuOverlay.setVisibility(View.VISIBLE);
            fabMain.animate().rotation(45f).setDuration(200).start();
            isMenuOpen = true;
        } else {
            // Zamykanie
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
                foodList.remove(position);
                adapter.notifyItemRemoved(position);

                // Zapisanie aktualnego stanu do pliku
                StorageHelper.saveAllFoodItems(MainActivity.this, foodList, FILE_NAME);
                Toast.makeText(MainActivity.this, "Usunięto: " + deletedItem.getName(), Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void updateAlarmStatus() {
        if (alarmStatusText != null) {
            alarmStatusText.setText(AlarmHelper.getNextAlarmStatus(this));
        }
        if (textToggleNotifications != null) {
            boolean isEnabled = AlarmHelper.isNotificationsEnabled(this);
            textToggleNotifications.setText(isEnabled ? "Wyłącz powiadomienia" : "Włącz powiadomienia");
            
            // Note: ic_lock_silent_mode_off might not exist on all versions, using standard ones
            fabToggleNotifications.setImageResource(isEnabled ? 
                    android.R.drawable.ic_lock_silent_mode : 
                    android.R.drawable.ic_lock_silent_mode_off);
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
