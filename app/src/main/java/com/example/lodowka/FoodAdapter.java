package com.example.lodowka;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        holder.dateText.setText("Data ważności: " + sdf.format(item.getExpiryDate().getTime()));
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
        TextView nameText;
        TextView dateText;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textViewFoodName);
            dateText = itemView.findViewById(R.id.textViewExpiryDate);
        }
    }
}
