package com.example.myapplication5;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FoodAdapter.OnItemClickListener {

    private RecyclerView foodRecyclerView;
    private TextView totalText;
    private Button checkoutButton;
    private FoodAdapter adapter;
    private List<Food> foodList;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        totalText = findViewById(R.id.totalText);
        checkoutButton = findViewById(R.id.checkoutButton);
    }

    private void setupRecyclerView() {
        foodList = createMockFoodData();
        adapter = new FoodAdapter(foodList, this);

        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        checkoutButton.setOnClickListener(v -> {
            if (totalAmount > 0) {
                Toast.makeText(this, "Order placed! Total: $" +
                        new DecimalFormat("#0.00").format(totalAmount), Toast.LENGTH_LONG).show();
                totalAmount = 0.0;
                updateTotalText();
            } else {
                Toast.makeText(this, "Add items to your cart first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Food> createMockFoodData() {
        List<Food> foods = new ArrayList<>();
        foods.add(new Food("Margherita Pizza", "Classic tomato, mozzarella, and fresh basil", 12.99, "🍕"));
        foods.add(new Food("Chicken Burger", "Grilled chicken with lettuce, tomato, and mayo", 9.99, "🍔"));
        foods.add(new Food("Caesar Salad", "Fresh romaine lettuce with caesar dressing", 8.49, "🥗"));
        foods.add(new Food("Beef Tacos", "Seasoned ground beef with fresh toppings", 10.99, "🌮"));
        foods.add(new Food("Chicken Wings", "Spicy buffalo wings with ranch dip", 11.49, "🍗"));
        foods.add(new Food("Pasta Carbonara", "Creamy pasta with bacon and parmesan", 13.99, "🍝"));
        foods.add(new Food("Fish & Chips", "Battered fish with crispy fries", 14.99, "🍟"));
        foods.add(new Food("Chocolate Cake", "Rich chocolate cake with cream frosting", 6.99, "🍰"));
        return foods;
    }

    @Override
    public void onAddButtonClick(Food food) {
        totalAmount += food.getPrice();
        updateTotalText();
        Toast.makeText(this, food.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalText() {
        totalText.setText("Total: $" + new DecimalFormat("#0.00").format(totalAmount));
    }
}