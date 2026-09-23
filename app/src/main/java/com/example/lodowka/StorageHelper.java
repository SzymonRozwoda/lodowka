package com.example.lodowka;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class StorageHelper {

    private static boolean isMigrated = false;

    public static synchronized void migrateLegacyDataIfNeeded(Context context) {
        if (isMigrated) return;
        File legacyFile = new File(context.getFilesDir(), "foods.txt");
        if (legacyFile.exists()) {
            ArrayList<FoodItem> legacyItems = loadFoodItemsFromTextFile(context, "foods.txt");
            if (!legacyItems.isEmpty()) {
                AppDatabase.getInstance(context).foodDao().insertAll(legacyItems);
            }
            legacyFile.delete();
        }
        isMigrated = true;
    }

    private static ArrayList<FoodItem> loadFoodItemsFromTextFile(Context context, String filename) {
        ArrayList<FoodItem> items = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(filename);
             Scanner scanner = new Scanner(fis)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length >= 2) {
                    String name = parts[0];
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(Long.parseLong(parts[1]));
                    int reminderDays = 3;
                    if (parts.length >= 3) {
                        try {
                            reminderDays = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            reminderDays = 3;
                        }
                    }
                    items.add(new FoodItem(name, date, reminderDays));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static void saveFoodItem(Context context, FoodItem item, String filename) {
        migrateLegacyDataIfNeeded(context);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getInstance(context).foodDao().insert(item);
        });
    }

    public static void saveAllFoodItems(Context context, ArrayList<FoodItem> items, String filename) {
        migrateLegacyDataIfNeeded(context);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            FoodDao dao = AppDatabase.getInstance(context).foodDao();
            dao.deleteAll();
            dao.insertAll(items);
        });
    }

    public static void deleteFoodItem(Context context, FoodItem item) {
        migrateLegacyDataIfNeeded(context);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getInstance(context).foodDao().delete(item);
        });
    }

    public static ArrayList<FoodItem> loadFoodItems(Context context, String filename) {
        migrateLegacyDataIfNeeded(context);
        List<FoodItem> list = AppDatabase.getInstance(context).foodDao().getAllFoodItemsSync();
        return new ArrayList<>(list);
    }
}
