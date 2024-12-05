package com.example.foodrecipeapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.SearchUserActivity;
import com.example.foodrecipeapp.adapter.RecentChatRecyclerAdapter;
import com.example.foodrecipeapp.model.ChatroomModel;
import com.example.foodrecipeapp.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentMessage extends Fragment {

    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private RecentChatRecyclerAdapter adapter;
    private List<ChatroomModel> chatroomList;

    public FragmentMessage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        searchButton = view.findViewById(R.id.message_search_btn);
        recyclerView = view.findViewById(R.id.recyler_view);

        chatroomList = new ArrayList<>();
        setupRecyclerView();

        Query chatroomsQuery = FirebaseUtil.allChatroomCollectionReference()
                .orderByChild("lastMessageTimestamp");

        chatroomsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatroomList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatroomModel chatroom = snapshot.getValue(ChatroomModel.class);
                    // Chỉ thêm các chatroom có tin nhắn và người dùng hiện tại là thành viên
                    if (chatroom != null &&
                            chatroom.getUserIds().contains(FirebaseUtil.currentUserId()) &&
                            chatroom.getLastMessage() != null &&
                            !chatroom.getLastMessage().isEmpty()) {
                        chatroomList.add(chatroom);
                    }
                }

                // Chỉ cập nhật adapter nếu có chat
                if (!chatroomList.isEmpty()) {
                    adapter.updateData(chatroomList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load chatrooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchUserActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void setupRecyclerView() {
        adapter = new RecentChatRecyclerAdapter(chatroomList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}