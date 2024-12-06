package com.example.foodrecipeapp.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class FirebaseUtil {

    private Context context;

    public FirebaseUtil(Context context) {
        this.context = context;
    }

    // Firebase Auth và Database instances
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    // Storage reference
    private static final StorageReference storageReference = storage.getReference();

    // Lấy ID người dùng hiện tại
    public static String currentUserId() {
        return firebaseAuth.getUid();
    }

    // Kiểm tra người dùng có đăng nhập hay không
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // Lấy tham chiếu đến thông tin người dùng hiện tại
    public static DatabaseReference currentUserDetails() {
        return firebaseDatabase.getReference("Users").child(currentUserId());
    }


    // Lấy Token FCM cho người dùng hiện tại
    public static void getFcmToken(FCMTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Lấy token thành công
                        String token = task.getResult();
                        callback.onSuccess(token);
                    } else {
                        // Xử lý lỗi khi không lấy được token
                        Log.e("FirebaseUtil", "Failed to get FCM token", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    // Định nghĩa callback để lấy token FCM
    public interface FCMTokenCallback {
        void onSuccess(String token);
        void onError(Exception e);
    }

    // Lấy tất cả người dùng
    public static DatabaseReference allUserCollectionReference() {
        return firebaseDatabase.getReference("Users");
    }

    // Lấy tham chiếu đến thông tin một người dùng cụ thể
    public static DatabaseReference getUserReference(String userId) {
        return firebaseDatabase.getReference("Users").child(userId);
    }

    // Lấy tham chiếu đến một phòng chat cụ thể
    public static DatabaseReference getChatroomReference(String chatroomId) {
        return firebaseDatabase.getReference("Chatrooms").child(chatroomId);
    }

    // Lấy danh sách tin nhắn của một phòng chat
    public static DatabaseReference getChatMessagesRef(String chatroomId) {
        return getChatroomReference(chatroomId).child("chats");
    }

    // Tạo ID cho một phòng chat
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Lấy danh sách tất cả các phòng chat
    public static DatabaseReference allChatroomCollectionReference() {
        return firebaseDatabase.getReference("Chatrooms");
    }

    // Lấy ID của người dùng khác trong phòng chat
    public static String getOtherUserId(List<String> userIds) {
        return userIds.get(0).equals(currentUserId()) ? userIds.get(1) : userIds.get(0);
    }

    // Đăng xuất
    public static void logout() {
        firebaseAuth.signOut();
    }

    // Lấy tham chiếu đến ảnh profile trong Firebase Storage
    public static StorageReference getCurrentProfilePicStorageRef() {
        return storageReference.child("profileImage").child(currentUserId());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return storageReference.child("profileImage").child(otherUserId);
    }

    // Lấy tên người dùng hiện tại
    public static void currentUserName(final UserNameCallback callback) {
        DatabaseReference userRef = firebaseDatabase.getReference("Users").child(currentUserId()).child("name");
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String name = task.getResult().getValue(String.class);
                callback.onSuccess(name);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public interface UserNameCallback {
        void onSuccess(String name);
        void onError(Exception e);
    }



}
