package com.example.foodrecipeapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.SearchUserActivity;

public class FragmentMessage extends Fragment {

    ImageButton searchButton;

    public FragmentMessage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Ánh xạ searchButton sau khi layout đã được inflate
        searchButton = view.findViewById(R.id.message_search_btn);

        // Thiết lập sự kiện click cho searchButton
        searchButton.setOnClickListener(v -> {
            // Sử dụng Intent để chuyển sang SearchUserActivity
            Intent intent = new Intent(getActivity(), SearchUserActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
