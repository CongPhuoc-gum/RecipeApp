package com.example.foodrecipeapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.adapter.ChatRecyclerAdapter;
import com.example.foodrecipeapp.model.ChatMessageModel;
import com.example.foodrecipeapp.model.ChatroomModel;
import com.example.foodrecipeapp.model.UserModel;
import com.example.foodrecipeapp.utils.AccessToken;
import com.example.foodrecipeapp.utils.FcmNotificationSender;
import com.example.foodrecipeapp.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private UserModel otherUser;
    private String chatroomId;
    private ChatroomModel chatroomModel;
    private ChatRecyclerAdapter adapter;

    private EditText messageInput;
    private ImageButton sendMessageBtn, backBtn;
    private TextView otherUsername;
    private RecyclerView recyclerView;
    private ImageView profilePicImageView;

    private ValueEventListener messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = (UserModel) getIntent().getSerializableExtra("otherUser");
        if (otherUser == null || TextUtils.isEmpty(otherUser.getUid())) {
            Log.e("ChatActivity", "Missing or invalid otherUser data");
            finish();
            return;
        }

        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUid());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePicImageView = findViewById(R.id.profile_pic_image_view);

        otherUsername.setText(otherUser.getName());
        Glide.with(this)
                .load(TextUtils.isEmpty(otherUser.getProfileImage()) ? R.drawable.image_profile : otherUser.getProfileImage())
                .placeholder(R.drawable.image_profile)
                .circleCrop()
                .into(profilePicImageView);

        backBtn.setOnClickListener(v -> onBackPressed());
        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                sendMessageToUser(message);
            }
        });

        setupChatRecyclerView();
        getOrCreateChatroomModel();
    }

    void setupChatRecyclerView() {
        adapter = new ChatRecyclerAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        DatabaseReference messagesRef = FirebaseUtil.getChatroomReference(chatroomId).child("chats");
        messagesListener = messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMessageModel> chatMessages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessageModel message = messageSnapshot.getValue(ChatMessageModel.class);
                    if (message != null) chatMessages.add(message);
                }
                adapter.updateData(chatMessages);
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

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chatroomModel = snapshot.getValue(ChatroomModel.class);
                } else {
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUid()),
                            ServerValue.TIMESTAMP,
                            "",
                            "",
                            new HashMap<>()
                    );
                    snapshot.getRef().setValue(chatroomModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Failed to retrieve or create chatroom: " + error.getMessage());
            }
        });
    }

    void sendMessageToUser(String message) {
        Map<String, Object> chatroomUpdates = new HashMap<>();
        chatroomUpdates.put("lastMessage", message);
        chatroomUpdates.put("lastMessageSenderId", FirebaseUtil.currentUserId());
        chatroomUpdates.put("lastMessageTimestamp", ServerValue.TIMESTAMP);

        FirebaseUtil.getChatroomReference(chatroomId)
                .updateChildren(chatroomUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatroomUpdate", "Chatroom updated successfully.");
                        sendNotificationToUser(message);
                    } else {
                        Log.e("ChatroomUpdate", "Failed to update chatroom.", task.getException());
                    }
                });

        ChatMessageModel chatMessageModel = new ChatMessageModel(
                message,
                FirebaseUtil.currentUserId(),
                ServerValue.TIMESTAMP
        );

        DatabaseReference chatsRef = FirebaseUtil.getChatroomReference(chatroomId).child("chats");
        String messageId = chatsRef.push().getKey();

        if (messageId != null) {
            chatsRef.child(messageId).setValue(chatMessageModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("NewMessage", "Message sent successfully.");
                            messageInput.setText("");
                        } else {
                            Log.e("NewMessage", "Failed to send message: " + task.getException().getMessage());
                        }
                    });
        }
    }

    private void sendNotificationToUser(String messageContent) {
        if (TextUtils.isEmpty(otherUser.getFcmToken())) {
            Log.e("ChatActivity", "FCM token not available for the user");
            return;
        }

        FirebaseUtil.currentUserName(new FirebaseUtil.UserNameCallback() {
            @Override
            public void onSuccess(String name) {
                String title = "Tin nhắn gửi từ " + name;  // Tên người dùng đã lấy được
                String body = messageContent;

                AccessToken accessToken = new AccessToken();
                accessToken.getAccessTokenAsync(new AccessToken.Callback() {
                    @Override
                    public void onSuccess(String token) {
                        FcmNotificationSender notificationSender = new FcmNotificationSender(
                                otherUser.getFcmToken(), title, body, ChatActivity.this
                        );
                        notificationSender.SendNotifications(token);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("ChatActivity", "Failed to get Access Token", e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("ChatActivity", "Failed to get user name", e);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            DatabaseReference messagesRef = FirebaseUtil.getChatroomReference(chatroomId).child("chats");
            messagesRef.removeEventListener(messagesListener);
        }
    }
}
