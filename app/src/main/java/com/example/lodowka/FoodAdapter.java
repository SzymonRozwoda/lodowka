package com.example.lodowka;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private ArrayList<FoodItem> foodList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public FoodAdapter(ArrayList<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.nameText.setText(item.getName());
        holder.dateText.setText("Data ważności: " + sdf.format(item.getExpiryDate().getTime())
                + " (przypomnij " + item.getReminderDaysBefore() + " dni przed)");

        // Obliczanie dni do końca ważności
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar expiry = (Calendar) item.getExpiryDate().clone();
        expiry.set(Calendar.HOUR_OF_DAY, 0);
        expiry.set(Calendar.MINUTE, 0);
        expiry.set(Calendar.SECOND, 0);
        expiry.set(Calendar.MILLISECOND, 0);

        long diffDays = (expiry.getTimeInMillis() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24);

        if (diffDays <= item.getReminderDaysBefore()) {
            // Tymczasowo: czerwone tło karty oraz biały tekst dla wysoki kontrastu
            int redBg = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light);
            holder.cardView.setCardBackgroundColor(redBg);
            holder.nameText.setTextColor(Color.WHITE);
            holder.dateText.setTextColor(Color.WHITE);
        } else {
            // Przywróć domyślne kolory
            holder.cardView.setCardBackgroundColor(holder.defaultCardBgColor);
            holder.nameText.setTextColor(holder.defaultNameColor);
            holder.dateText.setTextColor(holder.defaultDateColor);
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateData(ArrayList<FoodItem> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameText;
        TextView dateText;
        int defaultNameColor;
        int defaultDateColor;
        int defaultCardBgColor;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            nameText = itemView.findViewById(R.id.textViewFoodName);
            dateText = itemView.findViewById(R.id.textViewExpiryDate);
            defaultNameColor = nameText.getCurrentTextColor();
            defaultDateColor = dateText.getCurrentTextColor();
            defaultCardBgColor = cardView.getCardBackgroundColor().getDefaultColor();
        }
    }
}
