package com.example.foodrecipeapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrecipeapp.utils.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SendImageActivity extends AppCompatActivity {

    private String url, sender_uid, other_uid;
    private ImageView imageView;
    private Uri imageUrl;
    private ProgressBar progressBar;
    private Button sendButton;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        imageView = findViewById(R.id.iv_sendImage);
        progressBar = findViewById(R.id.pb_sendImage);
        sendButton = findViewById(R.id.btn_sendImage);

        // Nhận dữ liệu từ intent
        url = getIntent().getStringExtra("url");
        sender_uid = getIntent().getStringExtra("uid");
        other_uid = getIntent().getStringExtra("ruid");
        imageUrl = Uri.parse(url);

        // Hiển thị ảnh
        imageView.setImageURI(imageUrl);

        // Thiết lập Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("ChatImages");

        sendButton.setOnClickListener(v -> uploadImage());
    }

    private void uploadImage() {
        if (imageUrl != null) {
            progressBar.setVisibility(View.VISIBLE);
            String fileName = System.currentTimeMillis() + ".jpg";
            StorageReference fileRef = storageReference.child(fileName);

            // Upload ảnh lên Firebase Storage
            fileRef.putFile(imageUrl).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    sendImageMessage(downloadUrl);
                    Toast.makeText(SendImageActivity.this, "Image sent successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Log.e("SendImageActivity", "Failed to get download URL", e);
                    Toast.makeText(SendImageActivity.this, "Failed to send image", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Log.e("SendImageActivity", "Failed to upload image", e);
                Toast.makeText(SendImageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }).addOnCompleteListener(task -> progressBar.setVisibility(View.GONE));
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendImageMessage(String imageUrl) {
        // Lưu thông tin ảnh vào Firebase Realtime Database
        DatabaseReference chatRef = FirebaseUtil.getChatroomReference(FirebaseUtil.getChatroomId(sender_uid, other_uid))
                .child("chats");

        String messageId = chatRef.push().getKey();
        if (messageId != null) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("message", imageUrl); // URL ảnh
            messageData.put("senderId", sender_uid);
            messageData.put("timestamp", System.currentTimeMillis());
            messageData.put("type", "image"); // Đánh dấu là tin nhắn ảnh

            chatRef.child(messageId).setValue(messageData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("SendImageActivity", "Image message sent successfully.");
                } else {
                    Log.e("SendImageActivity", "Failed to send image message.", task.getException());
                }
            });
        }
    }
}
