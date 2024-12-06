package com.example.foodrecipeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.ChatActivity;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.model.ChatroomModel;
import com.example.foodrecipeapp.model.UserModel;
import com.example.foodrecipeapp.utils.AndroidUtil;
import com.example.foodrecipeapp.utils.FirebaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RecentChatRecyclerAdapter extends RecyclerView.Adapter<RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    private List<ChatroomModel> chatroomList;
    private Context context;

    public RecentChatRecyclerAdapter(List<ChatroomModel> chatroomList, Context context) {
        this.chatroomList = new ArrayList<>(chatroomList);
        this.context = context;
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position) {
        ChatroomModel chatroom = chatroomList.get(position);

        List<String> userIds = chatroom.getUserIds();
        if (userIds != null && userIds.size() == 2) {
            // Lấy ID của người dùng khác trong chatroom
            String otherUserId = userIds.stream()
                    .filter(userId -> !userId.equals(FirebaseUtil.currentUserId()))
                    .findFirst()
                    .orElse(null);

            if (otherUserId != null) {
                // Truy vấn thông tin người dùng
                FirebaseUtil.getUserReference(otherUserId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                UserModel otherUser = task.getResult().getValue(UserModel.class);
                                if (otherUser != null) {
                                    holder.usernameText.setText(otherUser.getName());

                                    // Load ảnh profile
                                    Glide.with(context)
                                            .load(otherUser.getProfileImage() != null ?
                                                    otherUser.getProfileImage() : R.drawable.ic_profile)
                                            .placeholder(R.drawable.ic_profile)
                                            .circleCrop()
                                            .into(holder.profilePic);

                                    // Thêm sự kiện click để mở ChatActivity
                                    holder.itemView.setOnClickListener(v -> {
                                        if (otherUserId != null) {
                                            Intent intent = new Intent(context, ChatActivity.class);
                                            intent.putExtra("otherUser", otherUser); // Chuyển đối tượng UserModel
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, "Cannot open chat. User ID is null.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {

                                }
                            } else {
                                Toast.makeText(context, "Error loading user", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        if (FirebaseUtil.currentUserId().equals(chatroom.getLastMessageSenderId())) {
            // Tin nhắn do người dùng gửi
            holder.lastMessageText.setText("Bạn: " + chatroom.getLastMessage());
            holder.lastMessageText.setTypeface(null, android.graphics.Typeface.NORMAL); // Bình thường
        } else {
            // Tin nhắn từ người khác
            holder.lastMessageText.setText(chatroom.getLastMessage());
            holder.lastMessageText.setTypeface(null, android.graphics.Typeface.BOLD); // In đậm
        }

        // Kiểm tra và định dạng thời gian tin nhắn
        if (chatroom.getLastMessageTimestamp() instanceof Long) {
            holder.lastMessageTime.setText(formatTimestamp((Long) chatroom.getLastMessageTimestamp()));
        } else {
            holder.lastMessageTime.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return chatroomList.size();
    }

    public void updateData(List<ChatroomModel> newChatroomList) {
        // Lọc và sắp xếp danh sách chat có tin nhắn
        List<ChatroomModel> filteredChats = newChatroomList.stream()
                .filter(chat -> chat.getLastMessage() != null && !chat.getLastMessage().isEmpty())
                .collect(Collectors.toList());

        // Sắp xếp danh sách theo tin nhắn mới nhất
        Collections.sort(filteredChats, (chat1, chat2) -> {
            Long ts1 = (chat1.getLastMessageTimestamp() instanceof Long) ?
                    (Long) chat1.getLastMessageTimestamp() : 0L;
            Long ts2 = (chat2.getLastMessageTimestamp() instanceof Long) ?
                    (Long) chat2.getLastMessageTimestamp() : 0L;
            return ts2.compareTo(ts1);  // Sắp xếp giảm dần
        });

        chatroomList.clear();
        chatroomList.addAll(filteredChats);
        notifyDataSetChanged();
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, lastMessageText, lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
