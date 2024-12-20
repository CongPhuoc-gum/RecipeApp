package com.example.foodrecipeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.adapter.AdapterComment;
import com.example.foodrecipeapp.model.ModelComments;
import com.example.foodrecipeapp.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RecipeDetail extends AppCompatActivity {
    private ImageButton btnFavorite;
    private boolean isFavorite = false;
    private DatabaseReference favoritesRef;
    private String recipeId;  // Recipe ID from intent
    private String userId;    // Current logged-in user ID
    private Button btnChat;
    private ImageButton addCommentBtn;
    private RecyclerView commentsRv;
    private AlertDialog alertDialog;
    private ArrayList<ModelComments> commentsArrayList;
    private AdapterComment adapterComment;

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
        btnChat = findViewById(R.id.btn_chat);
        addCommentBtn = findViewById(R.id.addCommentBtn);
        commentsRv = findViewById(R.id.commentsRv);

        // Get the logged-in user ID from FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User is not logged in!", Toast.LENGTH_SHORT).show();
            finish(); // End activity if no user is logged in
            return;
        }

        // Get the recipeId from the intent
        recipeId = getIntent().getStringExtra("recipeId");

        if (recipeId == null || recipeId.isEmpty()) {
            Toast.makeText(this, "Invalid Recipe ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase reference
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(userId);

        // Load recipe details and comments
        fetchRecipeDetails(recipeId, title, description, ingredients, steps, recipeImage, country, servings, postedBy);
        loadComments();

        // Handle the back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Handle the favorite button
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Handle Add Comment button
        addCommentBtn.setOnClickListener(v -> addCommentDialog());

        // Initialize AlertDialog
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Please Wait")
                .setCancelable(false)
                .create();
    }

    private void loadComments() {
        commentsArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
        ref.child(recipeId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentsArrayList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ModelComments modelComments = ds.getValue(ModelComments.class);
                            if (modelComments != null) {
                                commentsArrayList.add(modelComments);
                            }
                        }
                        adapterComment = new AdapterComment(commentsArrayList, RecipeDetail.this);
                        commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RecipeDetail.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String comment = "";

    private void addCommentDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_comment_add, null);
        EditText commentEditText = dialogView.findViewById(R.id.commentEt);
        ImageButton btnBack = dialogView.findViewById(R.id.btn_back);
        Button btnSubmit = dialogView.findViewById(R.id.Btn_Submit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnBack.setOnClickListener(view -> alertDialog.dismiss());

        btnSubmit.setOnClickListener(view -> {
            comment = commentEditText.getText().toString().trim();
            if (TextUtils.isEmpty(comment.trim())) {
                Toast.makeText(RecipeDetail.this, "Enter your comment", Toast.LENGTH_SHORT).show();
            } else {
                alertDialog.dismiss();
                addComment();
            }
        });
    }

    private void addComment() {
        alertDialog.setMessage("Adding comment...");
        alertDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", timestamp);
        hashMap.put("recipeId", recipeId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("comment", comment);
        hashMap.put("uid", userId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
        ref.child(recipeId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    alertDialog.dismiss();
                    Toast.makeText(RecipeDetail.this, "Comment added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    alertDialog.dismiss();
                    Toast.makeText(RecipeDetail.this, "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
                        country.setText("Country: " + recipe.getCountry());
                        servings.setText("Servings: " + recipe.getServings());
                        postedBy.setText("Posted by: " + recipe.getUserName());

                        // Set chat button to start chat activity with the correct user
                        btnChat.setOnClickListener(v -> openChatActivity(recipe.getUserUid(), recipe.getUserName()));

                        // Load image with Glide
                        Glide.with(RecipeDetail.this).load(recipe.getImageUrl()).into(recipeImage);

                        // Check if the recipe is already in favorites
                        checkFavoriteStatus();
                    }
                } else {
                    Toast.makeText(RecipeDetail.this, "Recipe not found!", Toast.LENGTH_SHORT).show();
                    finish();
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
        btnFavorite.setImageResource(isFavorite ? R.drawable.heart_full : R.drawable.heart);
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

    private void openChatActivity(String otherUserId, String otherUserName) {
        if (otherUserId == null || otherUserId.isEmpty()) {
            Toast.makeText(this, "Invalid user!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RecipeDetail.this, ChatActivity.class);
        intent.putExtra("otherUserId", otherUserId);
        intent.putExtra("otherUserName", otherUserName);
        startActivity(intent);
    }
}
