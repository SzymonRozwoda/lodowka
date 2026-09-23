package com.example.lodowka;

import java.io.Serializable;
import java.util.Calendar;

public class FoodItem implements Serializable {
    private String name;
    private Calendar expiryDate;
    private int reminderDaysBefore;

    public FoodItem(String name, Calendar expiryDate) {
        this(name, expiryDate, 3);
    }

    public FoodItem(String name, Calendar expiryDate, int reminderDaysBefore) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public String getName() {
        return name;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public int getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(int reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    @Override
    public String toString() {
        return name + " (" + expiryDate.get(Calendar.DAY_OF_MONTH) + "/" +
                (expiryDate.get(Calendar.MONTH) + 1) + "/" + expiryDate.get(Calendar.YEAR) +
                ", przypomnienie: " + reminderDaysBefore + " dni przed)";
    }
}
