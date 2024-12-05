package com.example.foodrecipeapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FirebaseUtil {

    // Lấy ID người dùng hiện tại
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Kiểm tra người dùng có đăng nhập hay không
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // Lấy tham chiếu đến thông tin người dùng hiện tại trong Realtime Database
    public static DatabaseReference currentUserDetails() {
        return FirebaseDatabase.getInstance().getReference("Users").child(currentUserId());
    }
    public static DatabaseReference getUserReference(String userId) {
        return FirebaseDatabase.getInstance().getReference("Users").child(userId);
    }

    // Lấy tham chiếu đến tất cả người dùng trong Realtime Database
    public static DatabaseReference allUserCollectionReference() {
        return FirebaseDatabase.getInstance().getReference("Users");
    }

    // Lấy tham chiếu đến một cuộc trò chuyện cụ thể
    public static DatabaseReference getChatroomReference(String chatroomId) {
        return FirebaseDatabase.getInstance().getReference("Chatrooms").child(chatroomId);
    }

    // Lấy tham chiếu đến các tin nhắn trong một cuộc trò chuyện
    public static DatabaseReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).child("chats");
    }

    // Tạo ID cho cuộc trò chuyện giữa hai người dùng
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Lấy tham chiếu đến tất cả cuộc trò chuyện
    public static DatabaseReference allChatroomCollectionReference() {
        return FirebaseDatabase.getInstance().getReference("Chatrooms");
    }

    // Lấy thông tin người dùng khác trong cuộc trò chuyện
    public static DatabaseReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(currentUserId())) {
            return allUserCollectionReference().child(userIds.get(1));
        } else {
            return allUserCollectionReference().child(userIds.get(0));
        }
    }

    // Hàm để đăng xuất
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // Lấy tham chiếu đến ảnh profile của người dùng hiện tại trong Firebase Storage
    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(currentUserId());
    }

    // Lấy tham chiếu đến ảnh profile của người dùng khác trong Firebase Storage
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(otherUserId);
    }
}
