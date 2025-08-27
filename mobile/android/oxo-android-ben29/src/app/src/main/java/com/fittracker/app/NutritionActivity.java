package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class NutritionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        Button btnLogMeal = findViewById(R.id.btnLogMeal);
        Button btnBack = findViewById(R.id.btnBack);

        btnLogMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMealData();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadNutritionData();
    }

    private void loadNutritionData() {
        Intent nutritionIntent = new Intent("com.fittracker.NUTRITION_LOADED");
        nutritionIntent.putExtra("user_id", "user_12345");
        nutritionIntent.putExtra("daily_calories", 1850);
        nutritionIntent.putExtra("target_calories", 2200);
        nutritionIntent.putExtra("protein_intake", 85);
        nutritionIntent.putExtra("carb_intake", 220);
        nutritionIntent.putExtra("fat_intake", 65);
        nutritionIntent.putExtra("water_intake", "2.1L");
        nutritionIntent.putExtra("meal_plan", "balanced_diet");
        nutritionIntent.putExtra("dietary_restrictions", "lactose_intolerant");
        sendBroadcast(nutritionIntent);
    }

    private void logMealData() {
        Intent mealIntent = new Intent("com.fittracker.MEAL_LOGGED");
        mealIntent.putExtra("user_id", "user_12345");
        mealIntent.putExtra("meal_type", "lunch");
        mealIntent.putExtra("food_items", "chicken_salad,whole_grain_bread,apple");
        mealIntent.putExtra("calories", 450);
        mealIntent.putExtra("meal_time", System.currentTimeMillis());
        mealIntent.putExtra("location", "workplace_cafeteria");
        mealIntent.putExtra("photo_path", "/storage/meals/lunch_20240115.jpg");
        mealIntent.putExtra("nutritionist_notes", "good_protein_choice");
        sendBroadcast(mealIntent);

        Intent dietIntent = new Intent("com.fittracker.DIET_PROGRESS");
        dietIntent.putExtra("user_id", "user_12345");
        dietIntent.putExtra("weekly_compliance", 85);
        dietIntent.putExtra("weight_change", "-0.5kg");
        dietIntent.putExtra("body_fat_percentage", 12.5);
        dietIntent.putExtra("muscle_mass", "68kg");
        sendBroadcast(dietIntent);
    }
}
