package com.Smart_Farmer.smartfarmer;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class HelperClass {

    public String getfileExt(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void expandable(RelativeLayout relativeLayout, TextView tv1, TextView tv2, TextView tvHead1, TextView tvHead2) {
        if (relativeLayout.getVisibility() == View.VISIBLE) {
            relativeLayout.setVisibility(View.GONE);
            tvHead2.setTextColor(Color.parseColor("#FFFFFFFF"));
            tvHead1.setTextColor(Color.parseColor("#FFFFFFFF"));
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            tvHead1.setTextColor(Color.parseColor("#008305"));
            tvHead2.setTextColor(Color.parseColor("#008305"));
        }
        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.GONE);
        tv1.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        tv1.requestLayout();
    }

    public void SettingBtnDelete(TextView btnDelete, String s, Context context) {
        String email = context.getResources().getString(R.string.admin_gmail);
        if (s.equals(email)) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);

        }
    }

    public void DeletetingItem(String url) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference;
        storageReference = firebaseStorage.getReferenceFromUrl(url);
        storageReference.delete().addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(exception -> {
        });
    }

    public void SettingFAB(FloatingActionButton floatingActionButton, Context context) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("email")) {
                        String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                        String adminEmail = context.getResources().getString(R.string.admin_gmail);
                        if (email.equals(adminEmail)) {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        } else {
                            floatingActionButton.setVisibility(View.GONE);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
