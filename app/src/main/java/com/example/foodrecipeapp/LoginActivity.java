package com.example.foodrecipeapp;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView tv_newuser;
    ImageButton btn_back;
    Button btn_login;
    EditText inputemail, inputpassword;
    private FirebaseAuth firebaseAuth;
    private AlertDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        tv_newuser = findViewById(R.id.newuser);
        btn_back = findViewById(R.id.btn_back);
        btn_login = findViewById(R.id.loginBtn);
        inputemail = findViewById(R.id.input_email);
        inputpassword = findViewById(R.id.input_password);

//        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        tv_newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

//        click login btn
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String email = "", password = "";
    private void validateData() {
//        before login, let do some data
        email = inputemail.getText().toString().trim();
        password = inputpassword.getText().toString().trim();
//data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email Không Hợp Lệ...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Nhập Mật Khẩu...!", Toast.LENGTH_SHORT).show();
        } else {
           loginUser();
        }
    }

    private void loginUser() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View customView = inflater.inflate(R.layout.activity_custom_progress_dialog_login,null);
        builder.setView(customView);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();

//        Login user
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
//                        login success
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Đang Đăng Nhập....");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.exists()) {
                            String userType = "" + dataSnapshot.child("userType").getValue();
                            if ("user".equals(userType)) {
                                startActivity(new Intent(LoginActivity.this, Dashboard.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Loại người dùng không hợp lệ!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Dữ liệu người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}