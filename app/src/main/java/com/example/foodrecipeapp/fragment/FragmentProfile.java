package com.example.foodrecipeapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.MainActivity;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.model.UserModel;
import com.example.foodrecipeapp.utils.AndroidUtil;
import com.example.foodrecipeapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FragmentProfile extends Fragment {

    ImageView profile_pic;
    EditText profile_username;
    EditText profile_email;
    ConstraintLayout profile_monyeuthich;
    ConstraintLayout profile_yourrecipes;
    Button button_logout;
    ImageButton button_edit;
    ProgressBar progressBar;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo launcher để chọn ảnh
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profile_pic);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ các view
        profile_pic = view.findViewById(R.id.profile_pic);
        profile_username = view.findViewById(R.id.text_username_profile);
        profile_email = view.findViewById(R.id.text_email_profile);
        profile_monyeuthich = view.findViewById(R.id.profile_monyeuthich);
        profile_yourrecipes = view.findViewById(R.id.profile_yourrecipes);
        button_logout = view.findViewById(R.id.button_logout);
        button_edit = view.findViewById(R.id.button_edit);
        progressBar = view.findViewById(R.id.login_process_bar);

        // Sự kiện nút sửa
        button_edit.setOnClickListener(v -> {
            buttonEditClick();
        });

        // Sự kiện đăng xuất
        button_logout.setOnClickListener(v -> {


            FirebaseUtil.logout();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);


        });

        // Sự kiện chọn ảnh
        profile_pic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        // Lấy dữ liệu người dùng
        getUserData();

        return view;
    }

    void buttonEditClick() {
        String newUsername = profile_username.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            profile_username.setError("Tên người dùng phải dài hơn 3 ký tự");
            return;
        }

        currentUserModel.setName(newUsername);
        setInProcess(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        currentUserModel.setProfileImage(uri.toString());
                                        updateToFirebase();
                                    });
                        } else {
                            setInProcess(false);
                            AndroidUtil.showToast(getContext(), "Không thể tải ảnh lên");
                        }
                    });
        } else {
            updateToFirebase();
        }
    }

    void updateToFirebase() {
        currentUserModel.setUid(FirebaseUtil.currentUserId());
        FirebaseUtil.currentUserDetails().setValue(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProcess(false);
                    if (task.isSuccessful()) {
                        selectedImageUri = null; // Reset URI sau khi lưu thành công
                        AndroidUtil.showToast(getContext(), "Cập nhật thành công");
                    } else {
                        AndroidUtil.showToast(getContext(), "Cập nhật thất bại");
                    }
                });
    }

    void getUserData() {
        setInProcess(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProcess(false);
            if (task.isSuccessful() && task.getResult() != null) {
                currentUserModel = task.getResult().getValue(UserModel.class);
                if (currentUserModel != null && isAdded() && getActivity() != null) {
                    profile_username.setText(currentUserModel.getName());
                    profile_email.setText(currentUserModel.getEmail());

                    // Kiểm tra nếu người dùng đã chọn ảnh mới
                    if (selectedImageUri != null) {
                        AndroidUtil.setProfilePic(getContext(), selectedImageUri, profile_pic);
                    } else {
                        // Nếu không có ảnh mới, tải từ Firebase
                        Glide.with(this)
                                .load(currentUserModel.getProfileImage())
                                .placeholder(R.drawable.image_profile)
                                .error(R.drawable.image_profile)
                                .circleCrop()
                                .into(profile_pic);
                    }
                }
            } else {
                Log.e("FragmentProfile", "Failed to load user data: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    void setInProcess(boolean inProcess) {
        if (inProcess) {
            progressBar.setVisibility(View.VISIBLE);
            button_edit.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            button_edit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FragmentProfile", "Fragment attached to Activity.");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("FragmentProfile", "Fragment detached from Activity.");
    }

    @Override
    public void onResume() {
        super.onResume();

        // Nếu người dùng đã chọn ảnh mới, ưu tiên hiển thị ảnh đó
        if (selectedImageUri != null) {
            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profile_pic);
        } else {
            getUserData();
        }
    }
}
