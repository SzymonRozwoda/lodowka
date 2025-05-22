package com.example.lodowka1_2;

import android.content.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class StorageHelper {

    public static void saveFoodItem(Context context, FoodItem item, String filename) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(item.getName() + ";" + item.getExpiryDate().getTimeInMillis() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FoodItem> loadFoodItems(Context context, String filename) {
        ArrayList<FoodItem> items = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(filename);
             Scanner scanner = new Scanner(fis)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length == 2) {
                    String name = parts[0];
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(Long.parseLong(parts[1]));
                    items.add(new FoodItem(name, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
