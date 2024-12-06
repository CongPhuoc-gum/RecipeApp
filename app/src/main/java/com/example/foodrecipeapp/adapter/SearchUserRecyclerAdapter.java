package com.example.foodrecipeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.ChatActivity;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    private final List<UserModel> userList = new ArrayList<>();
    private final Context context;

    public SearchUserRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.usernameText.setText(user.getName());
        holder.emailText.setText(user.getEmail());

        // Sử dụng Glide để load ảnh
        Glide.with(context)
                .load(user.getProfileImage())
                .placeholder(R.drawable.image_profile) // Ảnh mặc định nếu link ảnh trống
                .circleCrop()
                .into(holder.profilePic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("otherUser", user); // Truyền đúng key
            context.startActivity(intent);

            // Log kiểm tra
            Log.d("SearchUserAdapter", "Opening ChatActivity with user: " + user.getName());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateData(List<UserModel> newUserList) {
        userList.clear();
        userList.addAll(newUserList);
        notifyDataSetChanged();
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView emailText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            emailText = itemView.findViewById(R.id.email_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
