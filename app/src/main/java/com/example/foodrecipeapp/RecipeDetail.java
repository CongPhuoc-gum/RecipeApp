package com.example.foodrecipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String recipeId;  // Recipe ID from intent
    private String userId;    // Current logged in user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize views
        btnFavorite = findViewById(R.id.btn_ic_favorite);
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView title = findViewById(R.id.text_title);
        TextView description = findViewById(R.id.text_description);
        TextView ingredients = findViewById(R.id.text_ingredients);
        TextView steps = findViewById(R.id.text_steps);
        ImageView recipeImage = findViewById(R.id.image_recipe);
        TextView country = findViewById(R.id.text_country);
        TextView servings = findViewById(R.id.text_servings);
        TextView postedBy = findViewById(R.id.recipe_username);

        // Get the recipeId from the intent
        Intent intent = getIntent();
        recipeId = intent.getStringExtra("recipeId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(userId);

        if (recipeId != null && !recipeId.isEmpty()) {
            fetchRecipeDetails(recipeId, title, description, ingredients, steps, recipeImage, country, servings, postedBy);
        } else {
            Toast.makeText(this, "Invalid Recipe ID!", Toast.LENGTH_SHORT).show();
        }

        // Handle the back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Handle the favorite button
        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void fetchRecipeDetails(String recipeId, TextView title, TextView description, TextView ingredients, TextView steps, ImageView recipeImage, TextView country, TextView servings, TextView postedBy) {
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId);

        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        title.setText(recipe.getName());
                        description.setText(recipe.getDescription());
                        ingredients.setText(recipe.getIngredients());
                        steps.setText(recipe.getSteps());
                        country.setText("Quốc gia: " + recipe.getCountry());
                        servings.setText("Khẩu phần: " + recipe.getServings());
                        postedBy.setText("Người đăng: " + recipe.getUserName());

                        // Load image with Glide
                        Glide.with(RecipeDetail.this).load(recipe.getImageUrl()).into(recipeImage);

                        // Check if the recipe is already in favorites
                        checkFavoriteStatus();
                    }
                } else {
                    Toast.makeText(RecipeDetail.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RecipeDetail.this, "Failed to load recipe details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFavoriteStatus() {
        favoritesRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isFavorite = dataSnapshot.exists();
                updateFavoriteButton();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RecipeDetail.this, "Failed to check favorite status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.heart_full);
        } else {
            btnFavorite.setImageResource(R.drawable.heart);
        }
    }

    private void toggleFavorite() {
        if (isFavorite) {
            favoritesRef.child(recipeId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        isFavorite = false;
                        updateFavoriteButton();
                        Toast.makeText(RecipeDetail.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(RecipeDetail.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show());
        } else {
            favoritesRef.child(recipeId).setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        isFavorite = true;
                        updateFavoriteButton();
                        Toast.makeText(RecipeDetail.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(RecipeDetail.this, "Failed to add to favorites", Toast.LENGTH_SHORT).show());
        }
    }
}


