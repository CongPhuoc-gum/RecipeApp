package com.example.foodrecipeapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.foodrecipeapp.ChatActivity;
import com.example.foodrecipeapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Map<String, String> data = message.getData();

        // Kiểm tra dữ liệu từ payload
        String title = data.get("title");
        String body = data.get("body");
        String chatRoomId = data.get("chatRoomId"); // ID phòng chat (nếu có)

        // Tạo rung khi nhận thông báo
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 200, 300};
        if (vibrator != null) {
            vibrator.vibrate(pattern, -1);
        }

        // Intent mở ChatActivity
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra("chatRoomId", chatRoomId); // Truyền chatRoomId vào intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notification")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.icon_app)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        // Tạo Notification Channel (cho Android O trở lên)
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "Notification";
            NotificationChannel channel = new NotificationChannel(
                    channelID, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            channel.setVibrationPattern(pattern);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            builder.setChannelId(channelID);
        }

        // Hiển thị thông báo
        if (notificationManager != null) {
            notificationManager.notify(100, builder.build());
        }
    }

}