package com.example.lodowka1_2;


import android.content.Context;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Storage {

    private static final String FILE_NAME = "food_items.dat";

    public static void saveFoodItems(Context context, ArrayList<FoodItem> foodList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE))) {
            oos.writeObject(foodList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FoodItem> loadFoodItems(Context context) {
        try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(FILE_NAME))) {
            return (ArrayList<FoodItem>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
