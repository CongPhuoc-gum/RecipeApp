package com.example.foodrecipeapp.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationSender {
    private  final String userFcmToken;
    private final String title;
    private final String body;
    private final Context context;

    private final String postUrl ="https://fcm.googleapis.com/v1/projects/recipe-app-f788b/messages:send";

    public FcmNotificationSender(String userFcmToken, String title, String body, Context context){
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;

    }
    public void SendNotifications(String KEY) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);

            messageObject.put("token", userFcmToken);
            messageObject.put("notification", notificationObject);

            mainObj.put("message", messageObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj,
                    response -> Log.d("FCMResponse", "Notification sent successfully: " + response.toString() + title),
                    error -> Log.e("FCMError", "Failed to send notification: " + error.getMessage())
            ) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("authorization", "Bearer " + KEY);
                    return header;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e("FCMNotificationSender", "Failed to create JSON payload", e);
        }
    }

}