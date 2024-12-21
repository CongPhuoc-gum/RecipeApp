package com.example.foodrecipeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.model.ChatMessageModel;
import com.example.foodrecipeapp.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatModelViewHolder> {

    private final List<ChatMessageModel> userList;
    private final Context context;

    // Constructor
    public ChatRecyclerAdapter(Context context, List<ChatMessageModel> chatMessages) {
        this.context = context;
        this.userList = chatMessages != null ? chatMessages : new ArrayList<>();
    }

    // Phương thức cập nhật dữ liệu
    public void updateData(List<ChatMessageModel> newMessages) {
        userList.clear();
        if (newMessages != null) {
            userList.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position) {
        ChatMessageModel model = userList.get(position);

        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // Tin nhắn của người dùng hiện tại
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);

            if ("text".equals(model.getMessageType())) {
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.rightChatImageview.setVisibility(View.GONE);
                holder.rightChatTextview.setText(model.getMessage());
            } else if ("image".equals(model.getMessageType())) {
                holder.rightChatTextview.setVisibility(View.GONE);
                holder.rightChatImageview.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(model.getMessage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)  // Hình hiển thị khi lỗi
                        .into(holder.rightChatImageview);
            }
        } else {
            // Tin nhắn của người khác
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);

            if ("text".equals(model.getMessageType())) {
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.leftChatImageview.setVisibility(View.GONE);
                holder.leftChatTextview.setText(model.getMessage());
            } else if ("image".equals(model.getMessageType())) {
                holder.leftChatTextview.setVisibility(View.GONE);
                holder.leftChatImageview.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(model.getMessage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(holder.leftChatImageview);
            }
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview;
        ImageView leftChatImageview, rightChatImageview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatImageview = itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageview = itemView.findViewById(R.id.right_chat_imageview);
        }
    }
}
