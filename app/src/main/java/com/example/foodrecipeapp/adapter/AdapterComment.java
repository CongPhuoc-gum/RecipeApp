package com.example.foodrecipeapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipeapp.R;
import com.example.foodrecipeapp.RecipeDetail;
import com.example.foodrecipeapp.model.ModelComments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment>{

    private Context context;
    private ArrayList<ModelComments> commentsArrayList;
    private FirebaseAuth firebaseAuth ;

    public AdapterComment(ArrayList<ModelComments> commentsArrayList, Context context) {
        this.commentsArrayList = commentsArrayList;
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment, viewGroup, false);
        return new HolderComment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holderComment, int position) {
        ModelComments modelComments = commentsArrayList.get(position);
        String id = modelComments.getId();
        String recipeId = modelComments.getRecipeId();
        String comment = modelComments.getComment();
        String userId = modelComments.getUid();

        String formattedDate = getFormattedDate(modelComments.getTimestamp());
        holderComment.dateTv.setText(formattedDate);
        holderComment.commentTv.setText(comment);

        loadUserDetails(modelComments, holderComment);
        holderComment.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null && userId.equals(firebaseAuth.getUid())){
                    deletecomment(modelComments, holderComment);
                }
            }
        });
    }

    private void deletecomment(ModelComments modelComments, HolderComment holderComment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Comments")
                .setMessage("Bạn có chắc xoá Comment này?")
                .setPositiveButton("DElETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Recipes");
                        ref.child(modelComments.getRecipeId())
                                .child("Comments")
                                .child(modelComments.getId())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "DeleTe....", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed To DeleTe" +e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void loadUserDetails(ModelComments modelComments, HolderComment holderComment) {
        String uid = modelComments.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue() != null
                                ? dataSnapshot.child("name").getValue().toString() : "Unknown";
                        String profileImage = dataSnapshot.child("profileImage").getValue() != null
                                ? dataSnapshot.child("profileImage").getValue().toString() : "";

                        holderComment.nameTv.setText(name);
                        try{
                            Glide.with(context)
                                    .load(profileImage)
                                    .placeholder(R.drawable.image_profile)
                                    .into(holderComment.profileTv);
                        }
                        catch (Exception e){
                            holderComment.profileTv.setImageResource(R.drawable.image_profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private String getFormattedDate(String timestamp) {
        try {
            // Convert the timestamp string to a long value
            long timeInMillis = Long.parseLong(timestamp);

            // Format the date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CHINA.getDefault());
            Date date = new Date(timeInMillis);

            return sdf.format(date); // Return the formatted date
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return an empty string if parsing fails
        }
    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder{
        ShapeableImageView profileTv;
        TextView nameTv, dateTv, commentTv;


        public HolderComment(@NonNull View view) {
            super(view);
            profileTv = itemView.findViewById(R.id.profileTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            commentTv = itemView.findViewById(R.id.commentTv);


        }
    }
}
