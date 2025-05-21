package com.example.lodowka1_2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends Activity {

    private EditText editTextName, editTextExpiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        editTextName = findViewById(R.id.editTextName);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextExpiryDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    editTextExpiryDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Method to add a food item
    public void addFoodItem(View view) {
        String name = editTextName.getText().toString();
        String expiryDateStr = editTextExpiryDate.getText().toString();

        if (name.isEmpty() || expiryDateStr.isEmpty()) {
            Toast.makeText(this, "Wszystkie pola muszą być wypełnione!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the date is valid
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar expiryDate = Calendar.getInstance();
            expiryDate.setTime(sdf.parse(expiryDateStr)); // Parse date string into Calendar instance

            if (expiryDate != null) {
                // Create FoodItem object
                FoodItem foodItem = new FoodItem(name, expiryDate);

                // Return result to MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("foodItem", foodItem);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Niepoprawna data!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Niepoprawna data!", Toast.LENGTH_SHORT).show();
        }
    }
}
