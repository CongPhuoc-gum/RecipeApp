package com.example.foodrecipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.foodrecipeapp.fragment.ViewPaperAdapter;
import com.example.foodrecipeapp.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

public class Dashboard extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewPaperAdapter adapter = new ViewPaperAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_library).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_messenger).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_favorites).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_profile).setChecked(true);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (itemId == R.id.navigation_library) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.navigation_messenger) {
                    viewPager.setCurrentItem(2);
                    return true;
                } else if (itemId == R.id.navigation_favorites) {
                    viewPager.setCurrentItem(3);
                    return true;
                }else if(itemId == R.id.navigation_profile){
                    viewPager.setCurrentItem(4);
                    return true;
                }
                return false;
            }
        });
        getFCMToken();
    }
    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    Log.d("FCM_TOKEN", "FCM Token: " + token);

                    // Lấy UID người dùng hiện tại từ FirebaseUtil
                    String userId = FirebaseUtil.currentUserId();  // Sử dụng phương thức currentUserId() từ FirebaseUtil

                    // Kiểm tra xem người dùng đã đăng nhập hay chưa
                    if (userId != null) {
                        // Thêm FCM token vào Realtime Database
                        DatabaseReference userRef = FirebaseUtil.currentUserDetails();  // Lấy tham chiếu đến thông tin người dùng
                        userRef.child("fcmToken").setValue(token)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("FCM_TOKEN", "FCM token added to database");
                                    } else {
                                        Log.e("FCM_TOKEN", "Failed to add FCM token to database");
                                    }
                                });
                    } else {
                        Log.e("FCM_TOKEN", "User is not logged in");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FCM_TOKEN", "Failed to get token", e);
                });
    }
}