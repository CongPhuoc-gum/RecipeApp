package com.example.foodrecipeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipeapp.adapter.ChatRecyclerAdapter;
import com.example.foodrecipeapp.model.ChatMessageModel;
import com.example.foodrecipeapp.model.ChatroomModel;
import com.example.foodrecipeapp.model.UserModel;
import com.example.foodrecipeapp.utils.AndroidUtil;
import com.example.foodrecipeapp.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private UserModel otherUser;
    private String chatroomId;
    private ChatroomModel chatroomModel;
    private ChatRecyclerAdapter adapter;

    private EditText messageInput;
    private ImageButton sendMessageBtn;
    private ImageButton backBtn;
    private TextView otherUsername;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Lấy thông tin UserModel từ Intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUid());

        // Ánh xạ các view
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener(v -> onBackPressed());
        otherUsername.setText(otherUser.getName());

        // Gửi tin nhắn khi người dùng nhấn nút gửi
        sendMessageBtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToUser(message);
            }
        });

        // Lấy hoặc tạo chatroom nếu chưa có
        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        // Lấy tham chiếu đến danh sách tin nhắn trong chatroom
        DatabaseReference messagesRef = FirebaseUtil.getChatroomReference(chatroomId).child("chats");

        // Lắng nghe dữ liệu thay đổi từ Firebase
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Tạo danh sách tin nhắn
                List<ChatMessageModel> chatMessages = new ArrayList<>();

                // Lấy từng tin nhắn từ snapshot
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessageModel message = messageSnapshot.getValue(ChatMessageModel.class);
                    if (message != null) {
                        chatMessages.add(message);
                    } else {
                        Log.e("ChatRecyclerView", "Message parsing failed for key: " + messageSnapshot.getKey());
                    }
                }

                // Gán dữ liệu vào adapter
                if (adapter == null) {
                    adapter = new ChatRecyclerAdapter(ChatActivity.this, chatMessages);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateData(chatMessages);
                }

                // Cuộn RecyclerView xuống tin nhắn cuối cùng
                if (!chatMessages.isEmpty()) {
                    recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatRecyclerView", "Failed to load messages: " + error.getMessage());
            }
        });
    }




    void sendMessageToUser(String message) {
        // Cập nhật thông tin chatroom (chỉ cập nhật các trường cần thiết)
        Map<String, Object> chatroomUpdates = new HashMap<>();
        chatroomUpdates.put("lastMessage", message);
        chatroomUpdates.put("lastMessageSenderId", FirebaseUtil.currentUserId());
        chatroomUpdates.put("lastMessageTimestamp", ServerValue.TIMESTAMP);

        FirebaseUtil.getChatroomReference(chatroomId)
                .updateChildren(chatroomUpdates) // Dùng updateChildren để chỉ cập nhật các trường cần thiết
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatroomUpdate", "Chatroom updated successfully.");
                    } else {
                        Log.e("ChatroomUpdate", "Failed to update chatroom.", task.getException());
                    }
                });

        // Tạo đối tượng ChatMessageModel cho tin nhắn mới
        ChatMessageModel chatMessageModel = new ChatMessageModel(
                message,
                FirebaseUtil.currentUserId(),
                ServerValue.TIMESTAMP
        );

        // Lưu tin nhắn vào chatroom
        DatabaseReference chatsRef = FirebaseUtil.getChatroomReference(chatroomId).child("chats");
        DatabaseReference newMessageRef = chatsRef.push(); // Tạo ID ngẫu nhiên cho tin nhắn
        Log.d("NewMessage", "Generated new message ID: " + newMessageRef.getKey());

        // Lưu tin nhắn mới vào cơ sở dữ liệu Firebase
        newMessageRef.setValue(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("NewMessage", "Message sent successfully.");
                        messageInput.setText(""); // Xóa ô nhập tin nhắn sau khi gửi thành công
                    } else {
                        Log.e("NewMessage", "Failed to send message: " + task.getException().getMessage());
                    }
                });
    }

    void getOrCreateChatroomModel() {
        DatabaseReference chatroomRef = FirebaseUtil.getChatroomReference(chatroomId);
        chatroomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().getValue(ChatroomModel.class);
                if (chatroomModel == null) {
                    // Tạo mới Chatroom nếu không tồn tại
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUid()),
                            ServerValue.TIMESTAMP,
                            "",
                            ""
                    );
                    chatroomRef.setValue(chatroomModel);
                }
            }
        });
    }






}
