package com.example.lodowka1_2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "expiration_notifications";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private ArrayList<FoodItem> foodList;
    private FoodAdapter adapter;
    private ListView listView;
    private EditText nameInput, dateInput;
    private Button addButton;
    private static final String FILE_NAME = "food_list.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sprawdzenie i żądanie uprawnienia do powiadomień
        checkNotificationPermission();

        // Utworzenie kanału powiadomień
        createNotificationChannel();

        listView = findViewById(R.id.listViewFoodItems);
        nameInput = findViewById(R.id.editTextName);
        dateInput = findViewById(R.id.editTextExpirationDate);
        addButton = findViewById(R.id.addButton);

        foodList = loadFoodList();  // Load the list of food items from file

        adapter = new FoodAdapter(this, foodList);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String expirationDate = dateInput.getText().toString();

            if (!name.isEmpty() && !expirationDate.isEmpty()) {
                try {
                    Calendar expiryDate = Calendar.getInstance();
                    expiryDate.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expirationDate)); // Parse date string into Calendar instance
                    FoodItem newItem = new FoodItem(name, expiryDate);
                    foodList.add(newItem);
                    adapter.notifyDataSetChanged();  // Update the list
                    saveFoodList();  // Save the modified list
                    checkExpiringSoon(newItem); // Sprawdź, czy produkt ma bliską datę ważności
                    Toast.makeText(this, "Produkt dodany", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Niepoprawny format daty!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Wszystkie pola muszą być wypełnione", Toast.LENGTH_SHORT).show();
            }
        });

        dateInput.setOnClickListener(v -> showDatePicker());

        // Długie kliknięcie - usuń element
        listView.setOnItemLongClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            FoodItem itemToRemove = foodList.get(position);
            foodList.remove(position);
            adapter.notifyDataSetChanged(); // Aktualizuj widok listy
            saveFoodList(); // Zapisz zaktualizowaną listę
            Toast.makeText(this, "Usunięto: " + itemToRemove.getName(), Toast.LENGTH_SHORT).show();
            return true; // Wskazuje, że zdarzenie zostało obsłużone
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Aktualizuj pole z datą po wyborze
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Save the list of food items to a JSON file
    private void saveFoodList() {
        Gson gson = new Gson();
        String json = gson.toJson(foodList);
        getSharedPreferences("app_data", MODE_PRIVATE)
                .edit()
                .putString(FILE_NAME, json)
                .apply();
    }

    // Load the list of food items from a JSON file
    private ArrayList<FoodItem> loadFoodList() {
        Gson gson = new Gson();
        String json = getSharedPreferences("app_data", MODE_PRIVATE)
                .getString(FILE_NAME, "[]");  // Default to empty JSON
        Type type = new TypeToken<ArrayList<FoodItem>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Jeśli uprawnienia nie zostały przyznane, poproś o nie
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Uprawnienia przyznane
                Toast.makeText(this, "Uprawnienie do powiadomień przyznane", Toast.LENGTH_SHORT).show();
            } else {
                // Uprawnienia odrzucone
                Toast.makeText(this, "Uprawnienie do powiadomień jest wymagane do poprawnego działania", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Powiadomienia o ważności";
            String description = "Kanał powiadomień o kończącej się dacie ważności produktów";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkExpiringSoon(FoodItem item) {
        Calendar now = Calendar.getInstance();
        long diff = item.getExpiryDate().getTimeInMillis() - now.getTimeInMillis();
        long daysLeft = diff / (1000 * 60 * 60 * 24);

        if (daysLeft <= 3) {
            sendNotification(item);
            RedMark(item);
        }
    }
    private void RedMark(FoodItem item){
    //do dokonczenia
    }
    private void sendNotification(FoodItem item) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setSmallIcon(R.drawable.lodowka)
                .setContentTitle("Zaraz skończy się termin ważnośći!")
                .setContentText("" + item.getName() + " - za 3 dni lub wcześniej!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
