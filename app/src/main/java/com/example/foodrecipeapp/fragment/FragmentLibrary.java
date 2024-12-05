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
    private String currentUserEmail;

    public FragmentLibrary() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // Khởi tạo FirebaseAuth để lấy email người dùng hiện tại
        mAuth = FirebaseAuth.getInstance();
        currentUserEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "";

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recipes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách công thức
        recipeList = new ArrayList<>();
        adapter = new Recipe_Adapter(getContext(), recipeList, R.layout.fragment_item_recipe,false); // Layout của FragmentLibrary
        recyclerView.setAdapter(adapter);

        // Kết nối Firebase và tải dữ liệu chỉ theo email người dùng
        loadRecipesFromFirebase();

        // Button thêm công thức
        btn_add_recipe = view.findViewById(R.id.btn_add_recipe);
        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRecipe.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadRecipesFromFirebase() {
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes");

        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear(); // Xóa dữ liệu cũ trước khi thêm mới

                // Duyệt qua các công thức trong Firebase và thêm công thức của người dùng vào danh sách
                loadUserRecipes(snapshot);

                // Cập nhật RecyclerView sau khi tải dữ liệu
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }

    // Tách riêng logic tải công thức của người dùng
    private void loadUserRecipes(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Recipe recipe = dataSnapshot.getValue(Recipe.class);
            if (recipe != null && recipe.getUserEmail() != null && recipe.getUserEmail().equals(currentUserEmail)) {
                recipeList.add(recipe); // Thêm công thức của người dùng vào danh sách
            }
        }
    }
}
