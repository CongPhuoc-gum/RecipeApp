package com.example.foodrecipeapp.model;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Object timestamp;


    public ChatMessageModel() {}


    public ChatMessageModel(String message, String senderId, Object timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
