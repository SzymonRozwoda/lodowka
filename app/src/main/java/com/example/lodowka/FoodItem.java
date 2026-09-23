package com.example.lodowka;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "food_items")
public class FoodItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private long expiryTimeMillis;
    private int reminderDaysBefore;

    public FoodItem() {
    }

    @Ignore
    public FoodItem(String name, Calendar expiryDate) {
        this(name, expiryDate, 3);
    }

    @Ignore
    public FoodItem(String name, Calendar expiryDate, int reminderDaysBefore) {
        this.name = name;
        this.expiryTimeMillis = expiryDate != null ? expiryDate.getTimeInMillis() : 0;
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public FoodItem(int id, String name, long expiryTimeMillis, int reminderDaysBefore) {
        this.id = id;
        this.name = name;
        this.expiryTimeMillis = expiryTimeMillis;
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpiryTimeMillis() {
        return expiryTimeMillis;
    }

    public void setExpiryTimeMillis(long expiryTimeMillis) {
        this.expiryTimeMillis = expiryTimeMillis;
    }

    public Calendar getExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(expiryTimeMillis);
        return calendar;
    }

    public void setExpiryDate(Calendar expiryDate) {
        if (expiryDate != null) {
            this.expiryTimeMillis = expiryDate.getTimeInMillis();
        }
    }

    public int getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(int reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    @Override
    public String toString() {
        Calendar expiryDate = getExpiryDate();
        return name + " (" + expiryDate.get(Calendar.DAY_OF_MONTH) + "/" +
                (expiryDate.get(Calendar.MONTH) + 1) + "/" + expiryDate.get(Calendar.YEAR) +
                ", przypomnienie: " + reminderDaysBefore + " dni przed)";
    }
}
