package com.example.foodrecipeapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    private int layoutId;  // Layout ID dùng cho việc xác định CardView nhỏ hay lớn
    private boolean isHomePage;  // Thêm flag để xác định là trang Home hay Library

    public Recipe_Adapter(Context context, List<Recipe> recipeList, int layoutId, boolean isHomePage) {
        this.context = context;
        this.recipeList = recipeList;
        this.layoutId = layoutId;
        this.isHomePage = isHomePage;  // Lưu flag isHomePage khi truyền vào
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

        // Nếu đang ở trang Home, thì hiển thị tất cả các công thức mà không kiểm tra người dùng
        if (!isHomePage) {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            // Nếu đang ở trang Library và công thức không phải của người dùng thì ẩn
            if (recipe.getUserEmail() != null && !recipe.getUserEmail().equals(currentUserEmail)) {
                holder.cardView.setVisibility(View.GONE); // Ẩn nếu không phải của người dùng
            } else {
                holder.cardView.setVisibility(View.VISIBLE); // Hiển thị nếu là công thức của người dùng
            }
        } else {
            // Nếu đang ở trang Home, luôn hiển thị công thức
            holder.cardView.setVisibility(View.VISIBLE);
        }

        // Cập nhật các trường thông tin của công thức
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeCountry.setText(recipe.getCountry());

        // Hiển thị tên người dùng (nếu có), nếu không hiển thị email
        String userInfo = recipe.getUserName() != null && !recipe.getUserName().isEmpty()
                ? recipe.getUserName()
                : recipe.getUserEmail();
        holder.recipeUsername.setText("Người đăng: " + userInfo);

        // Tải ảnh từ URL
        Glide.with(context)
                .load(recipe.getImageUrl())
                .into(holder.recipeImage);

        // Cập nhật sự kiện click
        holder.cardView.setOnClickListener(v -> {
            // Chuyển sang màn hình chi tiết công thức
            Intent intent = new Intent(context, RecipeDetail.class);
            intent.putExtra("recipeName", recipe.getRecipeName());
            intent.putExtra("description", recipe.getDescription());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("steps", recipe.getSteps());
            intent.putExtra("imageUrl", recipe.getImageUrl());
            intent.putExtra("country", recipe.getCountry());
            intent.putExtra("userName", recipe.getUserName());
            context.startActivity(intent);
        });

        // Hiển thị nút delete nếu ở trang Library
        if (layoutId == R.layout.fragment_item_recipe && holder.btnDelete != null) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                deleteRecipeFromFirebase(recipe.getRecipeName());
                recipeList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, recipeList.size());
            });
        } else if (holder.btnDelete != null) {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Đặt màu nền ngẫu nhiên cho CardView
        Random random = new Random();
        @SuppressLint("ResourceType") String[] lightColors = {
                context.getString(R.color.yellow_light_1),
                context.getString(R.color.yellow_light_2),
                context.getString(R.color.orange_light_1),
                context.getString(R.color.orange_light_2)
        };

        int randomIndex = random.nextInt(lightColors.length);
        int randomColor = Color.parseColor(lightColors[randomIndex]);
        holder.cardView.setCardBackgroundColor(randomColor);
    }

    private void deleteRecipeFromFirebase(String recipeName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        databaseReference.orderByChild("recipeName").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi xóa
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
        ImageButton btnDelete;

        public RecipeViewHolder(View view) {
            super(view);
            // Khởi tạo các view
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_description);
            recipeCountry = itemView.findViewById(R.id.recipe_country);
            recipeUsername = itemView.findViewById(R.id.recipe_username);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // Gán CardView theo layout
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

