package com.example.foodrecipeapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorites extends Fragment {
    private RecyclerView recyclerView;
    private Recipe_Adapter adapter;
    private List<Recipe> favoriteRecipes;
    private DatabaseReference favoritesRef, recipesRef;

    public FragmentFavorites() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recipess_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteRecipes = new ArrayList<>();
        adapter = new Recipe_Adapter(getContext(), favoriteRecipes, R.layout.fragment_item_recipe, false);
        recyclerView.setAdapter(adapter);

        // Firebase references
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(userId);
        recipesRef = FirebaseDatabase.getInstance().getReference("Recipes");

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteRecipes.clear(); // Clear the list to avoid duplicates

                // Iterate through the user's favorite recipes
                for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                    String recipeId = favoriteSnapshot.getKey();
                    if (recipeId != null) {
                        // Get the recipe details by ID from the "Recipes" node
                        recipesRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot recipeSnapshot) {
                                Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                                if (recipe != null) {
                                    favoriteRecipes.add(recipe); // Add the recipe to the list
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle database error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
