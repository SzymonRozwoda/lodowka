package com.example.lodowka1_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import java.util.Calendar;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<FoodItem> {

    private Context context;
    private List<FoodItem> foodItems;

    public FoodAdapter(Context context, List<FoodItem> foodItems) {
        super(context, 0, foodItems);
        this.context = context;
        this.foodItems = foodItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        }

        FoodItem currentItem = foodItems.get(position);

        TextView nameTextView = convertView.findViewById(R.id.foodName);
        TextView dateTextView = convertView.findViewById(R.id.foodDate);

        nameTextView.setText(currentItem.getName());
        dateTextView.setText(currentItem.getExpiryDate().get(Calendar.DAY_OF_MONTH) + "/" +
                (currentItem.getExpiryDate().get(Calendar.MONTH) + 1) + "/" +
                currentItem.getExpiryDate().get(Calendar.YEAR));

        return convertView;
    }
}
