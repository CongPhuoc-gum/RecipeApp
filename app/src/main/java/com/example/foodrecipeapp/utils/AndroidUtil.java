package com.example.foodrecipeapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodrecipeapp.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AndroidUtil {

    // Hiển thị Toast
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Chuyển thông tin người dùng vào Intent
    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("email", model.getEmail());
        intent.putExtra("name", model.getName());
        intent.putExtra("profileImage", model.getProfileImage());
        intent.putExtra("timestamp", model.getTimestamp());
        intent.putExtra("uid", model.getUid());
        intent.putExtra("userType", model.getUserType());
    }

    // Lấy thông tin người dùng từ Intent
    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setName(intent.getStringExtra("name"));
        userModel.setProfileImage(intent.getStringExtra("profileImage"));
        userModel.setTimestamp(intent.getLongExtra("timestamp", 0));
        userModel.setUid(intent.getStringExtra("uid"));
        userModel.setUserType(intent.getStringExtra("userType"));
        return userModel;
    }

    // Cập nhật ảnh đại diện người dùng từ Firebase Storage
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    // Lấy thông tin người dùng từ Realtime Database
    public static void getUserModelFromDatabase(String userId, final UserModelCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    callback.onCallback(userModel);
                } else {
                    callback.onCallback(null);  // Người dùng không tồn tại
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(null);  // Lỗi khi đọc dữ liệu
            }
        });
    }

    // Interface để callback dữ liệu người dùng
    public interface UserModelCallback {
        void onCallback(UserModel userModel);
    }
}
