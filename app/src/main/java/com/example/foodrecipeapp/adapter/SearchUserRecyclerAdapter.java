package com.example.foodrecipeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipeapp.ChatActivity;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.model.UserModel;
import com.example.foodrecipeapp.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    private List<UserModel> userList = new ArrayList<>();
    private Context context;

    public SearchUserRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.usernameText.setText(user.getName());
        holder.emailText.setText(user.getEmail());

        if (!user.getProfileImage().isEmpty()) {
            Uri imageUri = Uri.parse(user.getProfileImage());
            AndroidUtil.setProfilePic(context, imageUri, holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, user);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Phương thức cập nhật dữ liệu mới
    public void updateData(List<UserModel> newUserList) {
        userList.clear();
        userList.addAll(newUserList);
        notifyDataSetChanged(); // Cập nhật RecyclerView
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
