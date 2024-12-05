package com.example.foodrecipeapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class FogotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private AlertDialog progressDialog;

    ImageButton btn_back;
    Button btn_submit;
    EditText inputemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_password);

        btn_back = findViewById(R.id.btn_back);
        btn_submit = findViewById(R.id.btn_submit);
        inputemail = findViewById(R.id.input_email);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize progressDialog
        progressDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Processing...")
                .create();

        // Button Submit Listener
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        // Button Back Listener
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String email = "";

    private void validateData() {
        email = inputemail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Nhập Email.....", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không đúng định dạng.", Toast.LENGTH_SHORT).show();
        } else {
            recoverPassword();
        }
    }

    private void recoverPassword() {
        progressDialog.setMessage("Chúng tôi đã gửi Email đặt lại mật khẩu cho bạn tới " + email);
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(FogotPasswordActivity.this, "Thông tin để đặt lại mật khẩu đã gửi đến " + email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(FogotPasswordActivity.this, "Lỗi khi gửi mã !!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
