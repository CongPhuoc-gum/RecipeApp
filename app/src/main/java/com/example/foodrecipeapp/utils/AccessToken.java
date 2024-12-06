package com.example.foodrecipeapp.utils;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccessToken {

    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";

    public void getAccessTokenAsync(Callback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String jsonString = "{\n" +
                        "  \"type\": \"service_account\",\n" +
                        "  \"project_id\": \"recipe-app-f788b\",\n" +
                        "  \"private_key_id\": \"e90f75e433c303e760ab3522fa23115d8ec68873\",\n" +
                        "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDnPpXSnnNyZ9D+\\npn3Xouk2aQS5SDIKqZHL4CBcb/CMplt+W3t2OZ8wkITnrQRtOhrSoT23SnG7mmxK\\nAMAFG5DJpct7g0BRrHuM0cZEjY4iBfLBoBeVajJdN51l2ndjjtsaO8z4S3zUoDhb\\naQWGh1oXvBwb9o763Q4gc4Ue9xnRmXeEncUmowyUy8wb1sGtjo/HlSGvIHE+smOd\\nnu47b20b6ByHlkxLYA5oYFh52eeDsyLuW1HDmhe6nJSmJnczPzc4S1x0tjiGUlDk\\nmytmPcmeIcknBl+toJ0x+aFBASHD5U57DYm9qyQBGJL2SwxZ8dV98fek65d7uBir\\nVcQNxdhxAgMBAAECggEAMK5lgpU+ZxmRbyaULAL9FyoFJ09aT5HSommwzlz3dlio\\nPsHaDDHFwEbQX+h129vFXU26DR3kMxMnlG2zfu/Ga5kWbQ5cykTmLqdT1sc5IpL/\\nf8Eo7djROPwWC+TxyNkAabWoqSXVJfsD2kSR9/gDIKeGgGGHyKqkSHMhVXs+KXg/\\nmKjMFef2pXaZEn5p0ZN5ZWz+Mv/PRTFt1aW9+g5t9C+Df9Qu8acbQ3qAggeegS/g\\n4jDFlvQnSfG2H0JH+ibYWiiLIphG4TMX9l/o8JxfWnoWxamN9dnaYDRz3F+0xjAw\\nzKq4GedQG1957JdESS9ZJUS+XwPII1Ve3XdMDBv+FQKBgQD/KPkH9Oqr34udT4PL\\nMMcyMCc+Znc0wq+/0Ayr1cEvMaZZDbN0SswcvdV3kyYux2zNbiHx+oAbnJ3cbYwV\\n9vh8zNbldfxNf/mXmrrgO+Wxa/owvIW4+uNiBzJ15L2i5fZ0lhamUPvwEjyzk1qq\\n6R8mDAr5OqWq/Iz/MWmaarpX1QKBgQDoAXVdA8bjClSgDoY3KoEzNEqbjfuVWp8R\\nbNLaG31lN55KLgP8xIAN86wnFwIDov2vUfagVHR8ySDt6SukrglGDljwAj+rFZlv\\nse0b/ksNCt62AumklH0SmsZtC0T4/A7QAdX2R2UWx4/xvHkZ+zazT76UBlLC19MO\\neeMUZc7ILQKBgQDKyk+RyU8xJkuZzPZb6PqHosmtyFX5crmnYryPXSVaUsV4hXEY\\nfdHXDfC4RhTUnN6WLm+AF6z1RPYZmF77nftDhLFOUQUiuxEtMmZPjIszBTQkw9Ar\\nggpxgHLUS+WDr+y+IAMET9zLolLoDbbDmt+Mp4mZJslAKdQNhcH+XdCXuQKBgDEP\\n1eTUnqN2lMzAJYvW9jmAzmI01UYRQuApdjFfbNYRu+yN7JPwbfAFXptplyhzuFfx\\n6kifc37L/aWyEGOj1hw1foNKxnIEgE0JgE3SKcbZTJ2K4iPGruhXSKzYnD5W9z7v\\nlHKfSHZXv03sgGOJJ1kl/PRKngfMIjrGdcnjeaYFAoGAOftylid1GVM38YLJWH+t\\nVIA4MQLKXy0x2vbFNYC4Lv+vPSBW0Ade80o2lSUwcz3a0bP2RFCCrm/lvJqV9OaD\\n81jRpd+63oI3lvIIU1qCm6DNyZwTYRQJhAtxom3MS4aMCAhnNlDUgyhFsZqHAtYK\\nCgB5xPYyCZ46lcnf274oBVE=\\n-----END PRIVATE KEY-----\\n\",\n" +
                        "  \"client_email\": \"firebase-adminsdk-3ms3v@recipe-app-f788b.iam.gserviceaccount.com\",\n" +
                        "  \"client_id\": \"109610667020922027041\",\n" +
                        "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                        "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                        "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                        "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-3ms3v%40recipe-app-f788b.iam.gserviceaccount.com\",\n" +
                        "  \"universe_domain\": \"googleapis.com\"\n" +
                        "}\n";

                InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                        .createScoped(Arrays.asList(firebaseMessagingScope));
                googleCredentials.refresh();

                String token = googleCredentials.getAccessToken().getTokenValue();

                // Gửi kết quả về luồng chính (Main Thread)
                callback.onSuccess(token);
            } catch (IOException e) {
                callback.onError(e);
            }
        });
    }

    public interface Callback {
        void onSuccess(String accessToken);
        void onError(Exception e);
    }
}

