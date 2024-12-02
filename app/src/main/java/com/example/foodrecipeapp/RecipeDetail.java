package com.example.foodrecipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.fragment.FragmentHome;

public class RecipeDetail extends AppCompatActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView title = findViewById(R.id.text_title);
        TextView description = findViewById(R.id.text_description);
        TextView ingredients = findViewById(R.id.text_ingredients);
        TextView steps = findViewById(R.id.text_steps);
        ImageView recipeImage = findViewById(R.id.image_recipe);
        TextView country = findViewById(R.id.text_country);


        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String recipeName = intent.getStringExtra("recipeName");
        String desc = intent.getStringExtra("description");
        String ing = intent.getStringExtra("ingredients");
        String step = intent.getStringExtra("steps");
        String imageUrl = intent.getStringExtra("imageUrl");
        String recipeCountry = intent.getStringExtra("country");



        // Hiển thị dữ liệu
        title.setText(recipeName != null ? recipeName : "Tên công thức không có");
        description.setText(desc != null ? desc : "Không có mô tả");
        ingredients.setText(ing != null ? ing : "Thành phần chưa được cung cấp");
        steps.setText(step != null ? step : "Hướng dẫn chưa được cung cấp");
        country.setText(recipeCountry != null ? "Quốc gia: " + recipeCountry : "Quốc gia không xác định");
        
        // Load ảnh từ URL (sử dụng Glide)
        Glide.with(this).load(imageUrl).into(recipeImage);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
}