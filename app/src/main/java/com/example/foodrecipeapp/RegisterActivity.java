package com.example.foodrecipeapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodrecipeapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class RegisterActivity extends AppCompatActivity {
    TextView tv_user;
    ImageButton btn_back;
    Button registerButton;
    EditText inputname, inputemail, inputpassword, cpassword;
    private FirebaseAuth firebaseAuth;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        inputname = findViewById(R.id.input_name);
        inputemail = findViewById(R.id.input_email);
        inputpassword = findViewById(R.id.input_password);
        cpassword = findViewById(R.id.confirm_password);
        btn_back = findViewById(R.id.btn_back);
        registerButton = findViewById(R.id.registerBtn);
        tv_user = findViewById(R.id.newuser);

//        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

//        btn_back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

//        Button_register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


//        have user
        tv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }





    private String name = "", email = "", password = "";
    private void validateData() {
        name = inputname.getText().toString().trim();
        email = inputemail.getText().toString().trim();
        password = inputpassword.getText().toString().trim();
        String cPassword = cpassword.getText().toString().trim();

//        Data
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Nhập Tên Của Bạn...", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email Không Hợp Lệ...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Nhập Mật Khẩu...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(this, "Nhập Lại Mật Khẩu...!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(this, "Mật Khẩu Không Trùng Khớp!", Toast.LENGTH_SHORT).show();
        }else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        //        setup alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View customView = inflater.inflate(R.layout.activity_custom_progress_dialog,null);
        builder.setView(customView);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();

//        create user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
//account create
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        account creating fail
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Đang Lưu Thông Tin...");
//        TimeStamp
        long timestamp = System.currentTimeMillis();

//        get current user uid, since user is
        String uid = firebaseAuth.getUid();
//        setup data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "");
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Đang Tạo Tài Khoản...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, Dashboard.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}