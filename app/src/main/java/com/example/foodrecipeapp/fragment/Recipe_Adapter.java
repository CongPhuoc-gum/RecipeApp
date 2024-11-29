package com.example.foodrecipeapp.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.Recipe;

import java.util.List;

public class Recipe_Adapter extends RecyclerView.Adapter<Recipe_Adapter.RecipeViewHolder> {
    private Context context;
    private List<Recipe> recipeList;

    public Recipe_Adapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public Recipe_Adapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Recipe_Adapter.RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        // Set data to views
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeCountry.setText(recipe.getCountry());
        holder.recipeUsername.setText("Người đăng: " + recipe.getUserEmail());

        // Load image using Glide
        Glide.with(context)
                .load(recipe.getImageUrl())
                .into(holder.recipeImage);

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public  static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeDescription, recipeCountry, recipeUsername;
        public RecipeViewHolder(View view) {
            super(view);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_description);
            recipeCountry = itemView.findViewById(R.id.recipe_country);
            recipeUsername = itemView.findViewById(R.id.recipe_username);
        }
    }
}
