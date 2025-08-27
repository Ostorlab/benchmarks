package com.example.myapplication5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    
    private List<Food> foodList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAddButtonClick(Food food);
    }

    public FoodAdapter(List<Food> foodList, OnItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodEmoji, foodName, foodDescription, foodPrice;
        Button addButton;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodEmoji = itemView.findViewById(R.id.foodEmoji);
            foodName = itemView.findViewById(R.id.foodName);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            addButton = itemView.findViewById(R.id.addButton);

            addButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAddButtonClick(foodList.get(position));
                }
            });
        }

        void bind(Food food) {
            foodEmoji.setText(food.getEmoji());
            foodName.setText(food.getName());
            foodDescription.setText(food.getDescription());
            foodPrice.setText("$" + new DecimalFormat("#0.00").format(food.getPrice()));
        }
    }
}