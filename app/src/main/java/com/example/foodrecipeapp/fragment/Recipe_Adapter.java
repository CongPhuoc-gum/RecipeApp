package com.example.foodrecipeapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.Recipe;
import com.example.foodrecipeapp.RecipeDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Random;

public class Recipe_Adapter extends RecyclerView.Adapter<Recipe_Adapter.RecipeViewHolder> {
    private Context context;
    private List<Recipe> recipeList;
    private int layoutId;  // Layout ID for small or large CardView
    private boolean isHomePage;  // Flag to check if it's Home Page or Library

    public Recipe_Adapter(Context context, List<Recipe> recipeList, int layoutId, boolean isHomePage) {
        this.context = context;
        this.recipeList = recipeList;
        this.layoutId = layoutId;
        this.isHomePage = isHomePage;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isHomePage) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_home_item_recipe, parent, false);  // Home Page layout
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_item_recipe, parent, false);  // Library layout
        }
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        // Get the user UID for the current user
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // If it’s the home page, show all recipes
        if (isHomePage) {
            holder.cardView.setVisibility(View.VISIBLE);
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.GONE);  // Hide the delete button on the home page
            }
        }
        // If it’s the library page, show only the user's own recipes
        else if (!isHomePage && recipe.getUserUid().equals(currentUserUid)) {
            holder.cardView.setVisibility(View.VISIBLE);
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.VISIBLE);  // Show delete button for user's own recipes
                holder.btnDelete.setOnClickListener(v -> {
                    deleteRecipeFromFirebase(recipe.getRecipeId());  // Delete by recipeId
                    recipeList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, recipeList.size());
                });
            }
        }
        // Hide recipes not belonging to the current user on library page
        else if (!isHomePage) {
            holder.cardView.setVisibility(View.GONE);
        }

        // Bind data to the views
        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeCountry.setText("Quốc gia: " + recipe.getCountry());
        holder.recipeUsername.setText("Người đăng: " + recipe.getUserName());

        // Load recipe image with Glide
        Glide.with(context)
                .load(recipe.getImageUrl())
                .into(holder.recipeImage);

        // Set up click listener for opening recipe detail
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetail.class);
            intent.putExtra("recipeId", recipe.getRecipeId());  // Pass recipeId to the detail activity
            context.startActivity(intent);
        });

        // Set a random background color for the recipe card
        Random random = new Random();
        @SuppressLint("ResourceType") String[] lightColors = {
                context.getString(R.color.l_1),
                context.getString(R.color.l_2),
                context.getString(R.color.l_3),
                context.getString(R.color.l_4)
        };
        int randomIndex = random.nextInt(lightColors.length);
        int randomColor = Color.parseColor(lightColors[randomIndex]);
        holder.cardView.setCardBackgroundColor(randomColor);
    }

    private void handleFavoriteButton(Recipe recipe, RecipeViewHolder holder) {
        ImageButton btnFavorite = holder.btnFavorite;
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(currentUserUid);

        // Check if the recipe is already in the user's favorites
        favoritesRef.child(recipe.getRecipeId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnFavorite.setImageResource(R.drawable.heart_full);  // Filled heart if in favorites
                } else {
                    btnFavorite.setImageResource(R.drawable.heart);  // Empty heart if not in favorites
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Recipe_Adapter", "Error checking favorite status: " + databaseError.getMessage());
            }
        });

        // Handle favorite button click
        btnFavorite.setOnClickListener(v -> {
            favoritesRef.child(recipe.getRecipeId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        favoritesRef.child(recipe.getRecipeId()).removeValue();
                        btnFavorite.setImageResource(R.drawable.heart);  // Remove from favorites
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        favoritesRef.child(recipe.getRecipeId()).setValue(true);
                        btnFavorite.setImageResource(R.drawable.heart_full);  // Add to favorites
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Recipe_Adapter", "Error toggling favorite: " + databaseError.getMessage());
                }
            });
        });
    }


    private void fetchUserNameByUid(String userUid, TextView textView) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    if (userName != null && !userName.isEmpty()) {
                        textView.setText("Người đăng: " + userName);
                    } else {
                        textView.setText("Người đăng: Không rõ");
                    }
                } else {
                    textView.setText("Người đăng: Không rõ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Recipe_Adapter", "Error fetching username: " + databaseError.getMessage());
                textView.setText("Người đăng: Không rõ");
            }
        });
    }

    private void deleteRecipeFromFirebase(String recipeId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
        databaseReference.child(recipeId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Recipe_Adapter", "Recipe deleted successfully");
                    } else {
                        Log.e("Recipe_Adapter", "Error deleting recipe");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeDescription, recipeCountry, recipeUsername;
        CardView cardView;
        ImageButton btnFavorite, btnDelete;

        public RecipeViewHolder(View view) {
            super(view);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_description);
            recipeCountry = itemView.findViewById(R.id.recipe_country);
            recipeUsername = itemView.findViewById(R.id.recipe_username);
            btnFavorite = itemView.findViewById(R.id.btn_ic_favorite);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
