package com.example.foodrecipeapp.model;

import java.util.List;
import java.util.Map;

public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;
    private Object lastMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;
    private Map<String, ChatMessageModel> chats; // Thêm trường này

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Object lastMessageTimestamp, String lastMessageSenderId, String lastMessage, Map<String, ChatMessageModel> chats) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.chats = chats; // Gán giá trị cho chats
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Object getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Object lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String, ChatMessageModel> getChats() {
        return chats;
    }

    public void setChats(Map<String, ChatMessageModel> chats) {
        this.chats = chats;
    }
}
