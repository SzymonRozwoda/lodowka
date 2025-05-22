package com.example.lodowka1_2;

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
    private Calendar expiryCalendar = Calendar.getInstance();
    private static final String FILE_NAME = "foods.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        nameInput = findViewById(R.id.editTextName);
        dateInput = findViewById(R.id.editTextExpiryDate);
        Button saveBtn = findViewById(R.id.buttonAdd);

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
                FoodItem item = new FoodItem(name, (Calendar) expiryCalendar.clone());
                StorageHelper.saveFoodItem(this, item, FILE_NAME);
                finish();
            }
        });
    }
}
