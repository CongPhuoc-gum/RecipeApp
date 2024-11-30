package com.example.foodrecipeapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipeapp.adapter.SearchUserRecyclerAdapter;
import com.example.foodrecipeapp.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchUserRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> onBackPressed());

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString().trim();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Tên người dùng phải có ít nhất 3 ký tự");
                return;
            }
            setupSearchRecycleView(searchTerm);
        });
    }

    void setupSearchRecycleView(String searchTerm) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserModel> searchResults = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null && userModel.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                        searchResults.add(userModel);
                    }
                }

                if (searchResults.isEmpty()) {
                    Toast.makeText(SearchUserActivity.this, "Không tìm thấy người dùng nào", Toast.LENGTH_SHORT).show();
                }

                adapter.updateData(searchResults); // Cập nhật RecyclerView
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchUserActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
