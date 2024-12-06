package com.example.foodrecipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RecipeDetail extends AppCompatActivity {

    private ImageButton btnFavorite;
    private boolean isFavorite = false;
    private DatabaseReference favoritesRef;
    private String recipeName; // To keep track of the recipe name

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
        TextView servings = findViewById(R.id.text_servings); // Khẩu phần
        TextView postedBy = findViewById(R.id.recipe_username); // Người đăng

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        recipeName = intent.getStringExtra("recipeName");
        String desc = intent.getStringExtra("description");
        String ing = intent.getStringExtra("ingredients");
        String step = intent.getStringExtra("steps");
        String imageUrl = intent.getStringExtra("imageUrl");
        String recipeCountry = intent.getStringExtra("country");
        String recipeServings = intent.getStringExtra("servings"); // Khẩu phần
        String postedByUser = intent.getStringExtra("userName"); // Người đăng

        // Hiển thị dữ liệu
        title.setText(recipeName != null ? recipeName : "Tên công thức không có");
        description.setText(desc != null ? desc : "Không có mô tả");
        ingredients.setText(ing != null ? ing : "Thành phần chưa được cung cấp");
        steps.setText(step != null ? step : "Hướng dẫn chưa được cung cấp");
        country.setText(recipeCountry != null ? "Quốc gia: " + recipeCountry : "Quốc gia không xác định");

        // Hiển thị khẩu phần và người đăng với kiểm tra null
        servings.setText(recipeServings != null && !recipeServings.isEmpty() ? "Khẩu phần: " + recipeServings : "Khẩu phần không xác định");
        postedBy.setText(postedByUser != null && !postedByUser.isEmpty() ? "Người đăng: " + postedByUser : "Người đăng không xác định");

        // Load ảnh từ URL (sử dụng Glide)
        Glide.with(this).load(imageUrl).into(recipeImage);

        // Firebase setup for favorites
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");

        // Check if the recipe is already in favorites
        checkFavoriteStatus();

        // Handle favorite button click
        btnFavorite.setOnClickListener(v -> {
            if (isFavorite) {
                // Remove from favorites
                favoritesRef.child(recipeName).removeValue();
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.heart); // Default heart icon
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
                recipeData.put("servings", recipeServings);
                recipeData.put("postedBy", postedByUser);

                favoritesRef.child(recipeName).setValue(recipeData);
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.heart_full); // Filled heart icon
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle back button click
        btnBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void checkFavoriteStatus() {
        favoritesRef.child(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.heart_full); // Set filled heart icon
                } else {
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.heart); // Set default heart icon
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RecipeDetail.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
