package com.example.foodrecipeapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.foodrecipeapp.AddRecipe;
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

public class FragmentLibrary extends Fragment {

    private Button btn_add_recipe;
    private RecyclerView recyclerView;
    private Recipe_Adapter adapter;
    private List<Recipe> recipeList;

    private FirebaseAuth mAuth;
    private String currentUserUid;

    public FragmentLibrary() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Initialize FirebaseAuth to get the current user's UID
        mAuth = FirebaseAuth.getInstance();
        currentUserUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize recipe list
        recipeList = new ArrayList<>();
        adapter = new Recipe_Adapter(getContext(), recipeList, R.layout.fragment_item_recipe, false); // Use the library layout for the RecyclerView
        recyclerView.setAdapter(adapter);

        // Button to add a new recipe
        btn_add_recipe = view.findViewById(R.id.btn_add_recipe);
        btn_add_recipe.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddRecipe.class);
            startActivity(intent);
        });

        // Load recipes from Firebase
        loadRecipesFromFirebase();

        return view;
    }

    private void loadRecipesFromFirebase() {
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes");

        // Query Firebase to load only the recipes where the current user's UID matches the userUid of the recipe
        recipeRef.orderByChild("userUid").equalTo(currentUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear(); // Clear old data before adding new data

                // Loop through the recipes and add them to the list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeList.add(recipe); // Add only the user's recipes
                    }
                }

                // Update RecyclerView after loading data
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}
