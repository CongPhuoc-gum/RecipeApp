    package com.example.foodrecipeapp.fragment;

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
        private int layoutId;

        public Recipe_Adapter(Context context, List<Recipe> recipeList, int layoutId) {
            this.context = context;
            this.recipeList = recipeList;
            this.layoutId = layoutId; // Gán đúng biến layoutId
        }

        @NonNull
        @Override
        public Recipe_Adapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
            return new RecipeViewHolder(view, layoutId);
        }


        @Override
        public void onBindViewHolder(@NonNull Recipe_Adapter.RecipeViewHolder holder, int position) {
            Recipe recipe = recipeList.get(position);

            holder.recipeName.setText(recipe.getRecipeName());
            holder.recipeDescription.setText(recipe.getDescription());
            holder.recipeCountry.setText(recipe.getCountry());
            holder.recipeUsername.setText("Người đăng: " + recipe.getUserEmail());

            // Set image using Glide (No need for setImageResource anymore)
            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .into(holder.recipeImage);


            if (layoutId == R.layout.fragment_item_recipe) {
                holder.btnDelete.setOnClickListener(v -> {
                    // Xóa công thức khỏi danh sách
                    recipeList.remove(position);
                    notifyItemRemoved(position);  // Thông báo rằng một item đã bị xóa
                    notifyItemRangeChanged(position, recipeList.size());  // Cập nhật các item còn lại

                    // Xóa công thức khỏi Firebase
                    deleteRecipeFromFirebase(recipe.getRecipeName());
                });
            }


            holder.cardView.setOnClickListener(v -> {
                // Pass data to RecipeDetail activity
                Intent intent = new Intent(context, RecipeDetail.class);
                intent.putExtra("recipeName", recipe.getRecipeName());
                intent.putExtra("description", recipe.getDescription());
                intent.putExtra("ingredients", recipe.getIngredients());
                intent.putExtra("steps", recipe.getSteps());
                intent.putExtra("imageUrl", recipe.getImageUrl());
                intent.putExtra("country", recipe.getCountry());
                context.startActivity(intent);
            });

            // Set random background color for the card view
            Random random = new Random();
            int randomColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            holder.cardView.setCardBackgroundColor(randomColor);
        }

        private void deleteRecipeFromFirebase(String recipeName) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
            databaseReference.orderByChild("recipeName").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();  // Xóa công thức khỏi Firebase
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
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
            public RecipeViewHolder(View view, int layoutId) {
                super(view);
                // Khởi tạo các view
                recipeImage = itemView.findViewById(R.id.recipe_image);
                recipeName = itemView.findViewById(R.id.recipe_name);
                recipeDescription = itemView.findViewById(R.id.recipe_description);
                recipeCountry = itemView.findViewById(R.id.recipe_country);
                recipeUsername = itemView.findViewById(R.id.recipe_username);
                btnDelete = itemView.findViewById(R.id.btnDeleteCard);

                // Kiểm tra và gán cardView đúng cách
                if (layoutId == R.layout.fragment_home_item_recipe) {
                    cardView = itemView.findViewById(R.id.card_view_small);  // FragmentHome layout
                } else if (layoutId == R.layout.fragment_item_recipe) {
                    cardView = itemView.findViewById(R.id.card_view); // FragmentLibrary layout
                }

                // Nếu cardView vẫn là null, kiểm tra lại ID trong XML
                if (cardView == null) {
                    throw new NullPointerException("CardView không được khởi tạo đúng cách trong layout.");
                }
            }
        }
    }
