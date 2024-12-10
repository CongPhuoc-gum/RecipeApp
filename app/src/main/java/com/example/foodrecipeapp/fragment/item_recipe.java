package com.example.foodrecipeapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.RecipeDetail;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class item_recipe extends Fragment {

    private static final String ARG_RECIPE_ID = "recipe_id";
    private String recipeId;

    public item_recipe() {
        // Required empty public constructor
    }

    public static item_recipe newInstance(String recipeId) {
        item_recipe fragment = new item_recipe();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getString(ARG_RECIPE_ID);
            Log.d("item_recipe", "Recipe ID: " + recipeId);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_recipe, container, false);

        ImageButton btnDelete = view.findViewById(R.id.btnDelete);
        CardView cardView = view.findViewById(R.id.cardView);

        // Click vào card để mở RecipeDetail
        cardView.setOnClickListener(v -> openRecipeDetail());

        // Click vào nút xóa
        btnDelete.setOnClickListener(v -> {
            if (recipeId != null && !recipeId.isEmpty()) {
                deleteRecipe();
            } else {
                Toast.makeText(getContext(), "Recipe ID is invalid", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void openRecipeDetail() {
        if (recipeId == null || recipeId.isEmpty()) {
            Toast.makeText(getContext(), "Recipe ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), RecipeDetail.class);
        intent.putExtra("RECIPE_ID", recipeId);
        startActivity(intent);
    }

    private void deleteRecipe() {
        if (recipeId == null || recipeId.isEmpty()) {
            Toast.makeText(getContext(), "Recipe ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes");
        recipeRef.child(recipeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Recipe has been deleted", Toast.LENGTH_SHORT).show();
                // Navigate back or update UI
                getParentFragmentManager().popBackStack();  // Quay lại màn hình trước
            } else {
                Toast.makeText(getContext(), "Error deleting recipe", Toast.LENGTH_SHORT).show();
                Log.e("item_recipe", "Failed to delete recipe: " + task.getException());
            }
        });
    }
}
