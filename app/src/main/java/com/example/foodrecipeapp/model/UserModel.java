package com.example.foodrecipeapp.model;

import android.os.Parcelable;

import java.io.Serializable;

public class UserModel implements Serializable  {
    private String email;
    private String name;
    private String profileImage;
    private long timestamp;
    private String uid;
    private String userType;
    private String fcmToken;

    public UserModel() {
        // Constructor rỗng để Firebase sử dụng khi lấy dữ liệu
    }

    public UserModel(String email, String name, String profileImage, long timestamp, String uid, String userType,String fcmToken) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
        this.uid = uid;
        this.userType = userType;
        this.fcmToken = fcmToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
