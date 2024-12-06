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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private RecyclerView recyclerView1, recyclerView2;
    private Recipe_Adapter adapter1, adapter2;
    private List<Recipe> recipeListAll;  // Danh sách tất cả công thức
    private List<Recipe> recipeListNewest; // Danh sách công thức mới nhất
    private Button btn_add_recipe;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo danh sách công thức
        recipeListAll = new ArrayList<>();
        recipeListNewest = new ArrayList<>();

        // Khởi tạo RecyclerView thứ nhất (Hiển thị tất cả công thức)
        recyclerView1 = view.findViewById(R.id.recipe_recycler_view);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter1 = new Recipe_Adapter(getContext(), recipeListAll, R.layout.fragment_home_item_recipe,true); // Layout của FragmentHome
        recyclerView1.setAdapter(adapter1);

        // Khởi tạo RecyclerView thứ hai (Hiển thị công thức mới nhất)
        recyclerView2 = view.findViewById(R.id.recipe_recycler_view_2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter2 = new Recipe_Adapter(getContext(), recipeListNewest, R.layout.fragment_home_item_recipe,true); // Layout của FragmentHome
        recyclerView2.setAdapter(adapter2);

        // Kết nối Firebase và tải dữ liệu
        loadRecipes();

        // Button thêm công thức mới
        btn_add_recipe = view.findViewById(R.id.btn_create_new);
        btn_add_recipe.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddRecipe.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadRecipes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeListAll.clear();  // Xóa dữ liệu cũ
                recipeListNewest.clear();  // Xóa dữ liệu công thức mới

                // Duyệt qua các công thức trong Firebase và thêm vào danh sách tất cả công thức
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeListAll.add(recipe);
                    }
                }

                // Lấy danh sách công thức mới nhất (giới hạn 5 công thức)
                loadNewestRecipes(); // Tách logic lấy công thức mới nhất

                // Cập nhật RecyclerView sau khi tải dữ liệu từ Firebase
                adapter1.notifyDataSetChanged(); // Cập nhật RecyclerView 1 (tất cả công thức)
                adapter2.notifyDataSetChanged(); // Cập nhật RecyclerView 2 (công thức mới nhất)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi kết nối Firebase thất bại
            }
        });
    }

    // Tách riêng logic lấy công thức mới nhất
    private void loadNewestRecipes() {
        int size = recipeListAll.size();
        for (int i = size - 1; i >= Math.max(0, size - 5); i--) {
            recipeListNewest.add(recipeListAll.get(i));
        }
    }
}
