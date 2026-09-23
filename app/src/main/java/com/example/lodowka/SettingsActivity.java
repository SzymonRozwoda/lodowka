package com.example.lodowka;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "lodowka_prefs";
    private static final String KEY_THEME = "app_theme";

    private MaterialSwitch switchNotifications, switchOnlyNearExpiry;
    private TextView textCurrentTime, textCurrentTheme, settingsAlarmStatusText;
    private LinearLayout layoutSetTime, layoutChangeTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicjalizacja widoków
        switchNotifications = findViewById(R.id.switch_notifications);
        switchOnlyNearExpiry = findViewById(R.id.switch_only_near_expiry);
        textCurrentTime = findViewById(R.id.text_current_time);
        textCurrentTheme = findViewById(R.id.text_current_theme);
        settingsAlarmStatusText = findViewById(R.id.settingsAlarmStatusText);
        layoutSetTime = findViewById(R.id.layout_set_time);
        layoutChangeTheme = findViewById(R.id.layout_change_theme);

        setupSettings();
    }

    private void setupSettings() {
        // 1. Powiadomienia - przełącznik główny
        boolean isEnabled = AlarmHelper.isNotificationsEnabled(this);
        switchNotifications.setChecked(isEnabled);

        boolean onlyNear = AlarmHelper.isOnlyNearExpiryEnabled(this);
        switchOnlyNearExpiry.setChecked(onlyNear);
        switchOnlyNearExpiry.setEnabled(isEnabled);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AlarmHelper.setNotificationsEnabled(this, isChecked);
            switchOnlyNearExpiry.setEnabled(isChecked);
            updateAlarmStatus();
            String msg = isChecked ? "Powiadomienia włączone" : "Powiadomienia wyłączone";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // 2. Powiadomienia tylko gdy blisko terminu
        switchOnlyNearExpiry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AlarmHelper.setOnlyNearExpiryEnabled(this, isChecked);
            String msg = isChecked
                    ? "Powiadomienia tylko przy krótkim terminie"
                    : "Powiadomienia codziennie";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // 3. Godzina powiadomienia
        layoutSetTime.setOnClickListener(v -> showTimePickerDialog());

        // 4. Motyw
        layoutChangeTheme.setOnClickListener(v -> showThemeSelectionDialog());

        updateUI();
    }

    private void updateUI() {
        // Aktualizacja czasu
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int hour = prefs.getInt("alarm_hour", 9);
        int minute = prefs.getInt("alarm_minute", 0);
        textCurrentTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

        // Aktualizacja nazwy motywu
        int themeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        String themeName = "Systemowy";
        if (themeMode == AppCompatDelegate.MODE_NIGHT_NO) themeName = "Jasny";
        else if (themeMode == AppCompatDelegate.MODE_NIGHT_YES) themeName = "Ciemny";
        textCurrentTheme.setText(themeName);

        updateAlarmStatus();
    }

    private void updateAlarmStatus() {
        if (settingsAlarmStatusText != null) {
            settingsAlarmStatusText.setText(AlarmHelper.getNextAlarmStatus(this));
        }
    }

    private void showTimePickerDialog() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentHour = prefs.getInt("alarm_hour", 9);
        int currentMinute = prefs.getInt("alarm_minute", 0);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, hourOfDay, minute) -> {
            AlarmHelper.saveAlarmTime(this, hourOfDay, minute);
            AlarmHelper.setDailyAlarm(this);
            updateUI();
            Toast.makeText(this, "Godzina powiadomienia zmieniona", Toast.LENGTH_SHORT).show();
        }, currentHour, currentMinute, true);

        if (timePickerDialog.getWindow() != null) {
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        timePickerDialog.show();
    }

    private void showThemeSelectionDialog() {
        String[] themes = {"Jasny", "Ciemny", "Systemowy"};
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentTheme = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        int checkedItem = 2; // Systemowy
        if (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) checkedItem = 0;
        else if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) checkedItem = 1;

        new AlertDialog.Builder(this)
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
                    updateUI();
                    dialog.dismiss();
                })
                .show();
    }
}
