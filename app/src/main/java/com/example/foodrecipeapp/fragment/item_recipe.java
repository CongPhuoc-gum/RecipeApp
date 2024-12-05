package com.example.foodrecipeapp.fragment;

import android.content.Intent;
import android.os.Bundle;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link item_recipe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class item_recipe extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String ARG_RECIPE_ID = "recipe_id";
    private String recipeId;


    public item_recipe() {
        // Required empty public constructor
    }

    public static item_recipe newInstance(String param1, String param2) {
        item_recipe fragment = new item_recipe();
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
            recipeId = getArguments().getString(ARG_RECIPE_ID);  // Lấy recipeId từ Bundle
            Log.d("item_recipe", "Recipe ID: " + recipeId);  // Log để kiểm tra ID
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_recipe, container, false);

        ImageButton btnDelete = view.findViewById(R.id.btnDelete);
        CardView cardView = view.findViewById(R.id.cardView);

        // Khi nhấn vào CardView, mở màn hình chi tiết công thức
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RecipeDetail.class);
                if (recipeId != null && !recipeId.isEmpty()) {
                    intent.putExtra("RECIPE_ID", recipeId);
                }
                startActivity(intent);
            }
        });

        // Khi nhấn vào nút xóa
        btnDelete.setOnClickListener(v -> {
            if (recipeId != null && !recipeId.isEmpty()) {
                deleteRecipe();
            } else {
                Toast.makeText(getContext(), "ID món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void deleteRecipe() {
        if (recipeId == null || recipeId.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy ID món ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tham chiếu đến Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = database.getReference("recipes");

        // Xóa công thức khỏi Firebase
        recipeRef.child(recipeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Công thức đã được xóa", Toast.LENGTH_SHORT).show();
                // Optional: Cập nhật UI hoặc thực hiện hành động khác sau khi xóa thành công
            } else {
                Toast.makeText(getContext(), "Lỗi khi xóa công thức", Toast.LENGTH_SHORT).show();
            }
        });
    }
}