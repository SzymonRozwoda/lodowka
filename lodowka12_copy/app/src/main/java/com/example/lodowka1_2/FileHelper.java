package com.example.lodowka1_2;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FileHelper {

    private static final String FILE_NAME = "food_list.json";

    public static void saveFoodList(Context context, ArrayList<FoodItem> foodList) {
        Gson gson = new Gson();
        String json = gson.toJson(foodList);
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FoodItem> loadFoodList(Context context) {
        Gson gson = new Gson();
        String json = null;
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            json = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<FoodItem>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
