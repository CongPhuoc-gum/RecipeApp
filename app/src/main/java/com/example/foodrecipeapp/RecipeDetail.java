package com.example.foodrecipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.fragment.FragmentHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RecipeDetail extends AppCompatActivity {
    private ImageButton btnFavorite;
    private boolean isFavorite = false;
    private DatabaseReference favoritesRef;


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        btnFavorite = findViewById(R.id.btn_ic_favorite);


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

        btnFavorite.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");

            if (isFavorite) {
                // Remove from favorites
                favoritesRef.child(recipeName).removeValue();
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.heart);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                // Add to favorites using HashMap
                HashMap<String, String> recipeData = new HashMap<>();
                recipeData.put("recipeName", recipeName);
                recipeData.put("description", desc);
                recipeData.put("ingredients", ing);
                recipeData.put("steps", step);
                recipeData.put("imageUrl", imageUrl);
                recipeData.put("country", recipeCountry);

                favoritesRef.child(recipeName).setValue(recipeData);
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.heart_full);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

}