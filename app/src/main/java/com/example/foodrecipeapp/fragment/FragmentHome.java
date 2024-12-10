package com.example.foodrecipeapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentHome extends Fragment {

    private RecyclerView recyclerView1, recyclerView2;
    private Recipe_Adapter adapter1, adapter2;
    private List<Recipe> recipeListAll;  // Danh sách tất cả recipes
    private List<Recipe> recipeListNewest;  // Danh sách recipes mới nhất
    private Button btnAddRecipe;

    public FragmentHome() {
        // Constructor trống
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo danh sách recipes
        recipeListAll = new ArrayList<>();
        recipeListNewest = new ArrayList<>();

        // RecyclerView cho tất cả các recipes
        recyclerView1 = view.findViewById(R.id.recipe_recycler_view);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter1 = new Recipe_Adapter(getContext(), recipeListAll, R.layout.fragment_home_item_recipe, true);
        recyclerView1.setAdapter(adapter1);

        // RecyclerView cho các recipes mới nhất
        recyclerView2 = view.findViewById(R.id.recipe_recycler_view_2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter2 = new Recipe_Adapter(getContext(), recipeListNewest, R.layout.fragment_home_item_recipe, true);
        recyclerView2.setAdapter(adapter2);

        // Button thêm recipe mới
        btnAddRecipe = view.findViewById(R.id.btn_create_new);
        btnAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddRecipe.class);
            startActivity(intent);
        });

        // Load dữ liệu từ Firebase
        loadRecipes();

        return view;
    }

    private void loadRecipes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeListAll.clear();
                recipeListNewest.clear();

                // Lấy dữ liệu từ Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeListAll.add(recipe);
                    }
                }

                // Lấy 5 recipes mới nhất (theo thứ tự ngược lại)
                loadNewestRecipes();

                // Cập nhật RecyclerView
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi lỗi xảy ra
            }
        });
    }

    private void loadNewestRecipes() {
        // Đảo ngược danh sách để lấy 5 mục cuối
        Collections.reverse(recipeListAll);
        for (int i = 0; i < Math.min(5, recipeListAll.size()); i++) {
            recipeListNewest.add(recipeListAll.get(i));
        }
        // Đảo lại danh sách về trạng thái ban đầu
        Collections.reverse(recipeListAll);
    }
}
