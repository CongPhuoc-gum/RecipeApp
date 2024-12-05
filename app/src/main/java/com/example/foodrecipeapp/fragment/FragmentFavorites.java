package com.example.foodrecipeapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentFavorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFavorites extends Fragment {
    private RecyclerView recyclerView;
    private Recipe_Adapter adapter;
    private List<Recipe> favoriteRecipes;
    private DatabaseReference favoritesRef;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentFavorites() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFavorites.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFavorites newInstance(String param1, String param2) {
        FragmentFavorites fragment = new FragmentFavorites();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recipess_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteRecipes = new ArrayList<>();
        adapter = new Recipe_Adapter(getContext(), favoriteRecipes, R.layout.fragment_item_recipe);
        recyclerView.setAdapter(adapter);

        // Reference to Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteRecipes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        favoriteRecipes.add(recipe);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}