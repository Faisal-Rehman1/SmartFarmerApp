package com.Smart_Farmer.smartfarmer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CropsAdapter extends FirebaseRecyclerAdapter<Add_Crops_Model, CropsAdapter.myViewHelper> {

    HelperClass helperClass = new HelperClass();

    public CropsAdapter(@NonNull FirebaseRecyclerOptions<Add_Crops_Model> options) {
        super(options);
    }

    @NonNull
    @Override

    public myViewHelper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_all_crops, parent, false); //return view
        return new myViewHelper(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHelper holder, @SuppressLint("RecyclerView") int position, @NonNull Add_Crops_Model model) {
        String uid = model.getCropUid();
        String cropNameSin = model.getCropNameSindhi();
        String cropNameEng = model.getCropNameEng();
        String cropImageUrl = model.getCropImg();

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(holder.itemView.getContext(), Trigered_Crop.class);
            i.putExtra("uid", uid);
            i.putExtra("cropNameSin", cropNameSin);
            i.putExtra("cropNameEng", cropNameEng);
            i.putExtra("cropImageUrl", cropImageUrl);
            holder.itemView.getContext().startActivity(i);
        });
        holder.tv_CropNameEng.setText(model.getCropNameEng());
        holder.tv_CropNameSin.setText(model.getCropNameSindhi());

        Glide.with(holder.img.getContext())
                .load(model.getCropImg())
                .placeholder(R.drawable.ic_launcher_background)
                .fitCenter()
                .error(R.drawable.ic_crop)
                .into(holder.img);

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder bu = new AlertDialog.Builder(holder.btnDelete.getContext());
            bu.setTitle("Are you sure?");
            bu.setMessage("Deleted Data Can't be Undo");


            bu.setPositiveButton("Delete", (dialogInterface, i) -> {
                FirebaseDatabase.getInstance().getReference().child(new Home_Screen().All_Crop_Refernece).child(Objects.requireNonNull(getRef(position).getKey())).removeValue();
                helperClass.DeletetingItem(model.getCropImg());
            });
            bu.setNegativeButton("Cancel", (dialogInterface, i) -> {

            });
            bu.show();

        });

    }

    class myViewHelper extends RecyclerView.ViewHolder {
        TextView tv_CropNameEng, tv_CropNameSin, btnDelete;
        ImageView img;


        public myViewHelper(@NonNull View itemView) {
            super(itemView);
            tv_CropNameEng = itemView.findViewById(R.id.single_all_crops_cropNameEng);
            tv_CropNameSin = itemView.findViewById(R.id.single_all_crops_cropNameSin);
            img = itemView.findViewById(R.id.single_all_crops_cropsImage);

            btnDelete = itemView.findViewById(R.id.single_all_crops_btnDelete);
            btnDelete.setVisibility(View.VISIBLE);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

            usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("email")) {
                            String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                            helperClass.SettingBtnDelete(btnDelete, email, btnDelete.getContext());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }
}