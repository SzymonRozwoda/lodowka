package com.example.lodowka1_2;

import java.io.Serializable;
import java.util.Calendar;

public class FoodItem implements Serializable {
    private String name;
    private Calendar expiryDate;

    public FoodItem(String name, Calendar expiryDate) {
        this.name = name;
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }
    

    @Override
    public String toString() {
        // Zwraca nazwę przedmiotu i datę w formacie "dd/MM/yyyy"
        return name + " - " + expiryDate.get(Calendar.DAY_OF_MONTH) + "/" +
                (expiryDate.get(Calendar.MONTH) + 1) + "/" +
                expiryDate.get(Calendar.YEAR);
    }
}
