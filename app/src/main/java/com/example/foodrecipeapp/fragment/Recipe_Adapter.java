package com.example.foodrecipeapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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
    private int layoutId;  // Layout ID for small or large CardView
    private boolean isHomePage;  // Flag to check if it's Home Page or Library

    public Recipe_Adapter(Context context, List<Recipe> recipeList, int layoutId, boolean isHomePage) {
        this.context = context;
        this.recipeList = recipeList;
        this.layoutId = layoutId;
        this.isHomePage = isHomePage;
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

        // Kiểm tra nếu đây là trang Library và lọc công thức theo đó
        if (!isHomePage) {
            String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (recipe.getUserEmail() != null && !recipe.getUserEmail().equals(currentUserEmail)) {
                holder.cardView.setVisibility(View.GONE);  // Ẩn nếu không phải công thức của người dùng
            } else {
                holder.cardView.setVisibility(View.VISIBLE);  // Hiển thị nếu là công thức của người dùng
                holder.btnDelete.setVisibility(View.VISIBLE);  // Hiển thị nút xóa
            }
        } else {
            holder.cardView.setVisibility(View.VISIBLE);  // Hiển thị tất cả công thức trên trang chủ
            holder.btnDelete.setVisibility(View.GONE);  // Ẩn nút xóa trên trang chủ
        }

        // Cập nhật thông tin công thức trên card
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeCountry.setText(recipe.getCountry());

        // Xử lý trường servings
        if (holder.recipeServings != null) {
            holder.recipeServings.setText("Khẩu phần: " + recipe.getServings());
        } else {
            Log.e("Recipe_Adapter", "recipeServings is null");
        }

        // Hiển thị tên người đăng
        if (recipe.getUserEmail() != null) {
            fetchUserNameByEmail(recipe.getUserEmail(), holder.recipeUsername);
        } else {
            holder.recipeUsername.setText("Người đăng: Không rõ");
        }

        // Tải hình ảnh từ URL bằng Glide
        Glide.with(context)
                .load(recipe.getImageUrl())
                .into(holder.recipeImage);

        // Đặt sự kiện click để xem chi tiết công thức
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetail.class);
            intent.putExtra("recipeName", recipe.getRecipeName());
            intent.putExtra("description", recipe.getDescription());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("steps", recipe.getSteps());
            intent.putExtra("imageUrl", recipe.getImageUrl());
            intent.putExtra("country", recipe.getCountry());
            intent.putExtra("userName", recipe.getUsername());  // Chuyển tên người dùng sang Activity chi tiết
            context.startActivity(intent);
        });

        // Xử lý sự kiện xóa công thức nếu ở Library
        if (!isHomePage && holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                deleteRecipeFromFirebase(recipe.getRecipeName());
                recipeList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, recipeList.size());
            });
        }

        // Đặt màu nền ngẫu nhiên cho card view
        Random random = new Random();
        @SuppressLint("ResourceType") String[] lightColors = {
                context.getString(R.color.l_1),
                context.getString(R.color.l_2),
                context.getString(R.color.l_3),
                context.getString(R.color.l_4)
        };

        int randomIndex = random.nextInt(lightColors.length);
        int randomColor = Color.parseColor(lightColors[randomIndex]);
        holder.cardView.setCardBackgroundColor(randomColor);
    }

    private void fetchUserNameByEmail(String email, TextView textView) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userName = snapshot.child("name").getValue(String.class);
                        if (userName != null && !userName.isEmpty()) {
                            textView.setText("Người đăng: " + userName);
                        } else {
                            textView.setText("Người đăng: " + email); // Fallback to email if name is not available
                        }
                    }
                } else {
                    textView.setText("Người đăng: " + email); // Fallback if user not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Recipe_Adapter", "Error fetching user name: " + databaseError.getMessage());
                textView.setText("Người đăng: " + email); // Fallback in case of error
            }
        });
    }

    private void deleteRecipeFromFirebase(String recipeName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        databaseReference.orderByChild("recipeName").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();  // Remove recipe from Firebase
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Recipe_Adapter", "Error deleting recipe: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeDescription, recipeCountry, recipeUsername, recipeServings;
        CardView cardView;
        ImageButton btnDelete;

        public RecipeViewHolder(View view) {
            super(view);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_description);
            recipeCountry = itemView.findViewById(R.id.recipe_country);
            recipeServings = itemView.findViewById(R.id.text_servings);
            recipeUsername = itemView.findViewById(R.id.recipe_username);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
