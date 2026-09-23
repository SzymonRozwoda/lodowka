package com.example.lodowka;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText dateInput;
    private EditText reminderDaysInput;
    private Calendar expiryCalendar = Calendar.getInstance();
    private static final String FILE_NAME = "foods.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        nameInput = findViewById(R.id.editTextName);
        dateInput = findViewById(R.id.editTextExpiryDate);
        reminderDaysInput = findViewById(R.id.editTextReminderDays);
        Button saveBtn = findViewById(R.id.buttonAdd);
        Button cancelBtn = findViewById(R.id.buttonCancel);

        dateInput.setOnClickListener(v -> {
            int year = expiryCalendar.get(Calendar.YEAR);
            int month = expiryCalendar.get(Calendar.MONTH);
            int day = expiryCalendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, y, m, d) -> {
                expiryCalendar.set(y, m, d);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                dateInput.setText(sdf.format(expiryCalendar.getTime()));
            }, year, month, day).show();
        });

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            if (!name.isEmpty()) {
                int reminderDays = 3;
                String reminderStr = reminderDaysInput.getText().toString().trim();
                if (!reminderStr.isEmpty()) {
                    try {
                        reminderDays = Integer.parseInt(reminderStr);
                    } catch (NumberFormatException e) {
                        reminderDays = 3;
                    }
                }
                FoodItem item = new FoodItem(name, (Calendar) expiryCalendar.clone(), reminderDays);
                StorageHelper.saveFoodItem(this, item, FILE_NAME);
                finish();
            }
        });

        cancelBtn.setOnClickListener(v -> finish());
    }
}
