package com.example.lodowka;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FoodDao {

    @Query("SELECT * FROM food_items ORDER BY expiryTimeMillis ASC")
    List<FoodItem> getAllFoodItemsSync();

    @Query("SELECT * FROM food_items ORDER BY expiryTimeMillis ASC")
    LiveData<List<FoodItem>> getAllFoodItemsLiveData();

    @Insert
    long insert(FoodItem item);

    @Insert
    void insertAll(List<FoodItem> items);

    @Update
    void update(FoodItem item);

    @Delete
    void delete(FoodItem item);

    @Query("DELETE FROM food_items")
    void deleteAll();
}
