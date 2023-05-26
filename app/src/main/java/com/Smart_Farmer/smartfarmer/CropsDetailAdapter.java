package com.Smart_Farmer.smartfarmer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class CropsDetailAdapter extends FirebaseRecyclerAdapter<Add_Crops_Infromation_Model, CropsDetailAdapter.myViewHelper> {

    HelperClass helperClass = new HelperClass();

    public CropsDetailAdapter(@NonNull FirebaseRecyclerOptions<Add_Crops_Infromation_Model> options) {
        super(options);
    }

    @NonNull
    @Override


    public myViewHelper onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_crop_details, parent, false); //return view
        return new myViewHelper(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CropsDetailAdapter.myViewHelper holder, @SuppressLint("RecyclerView") int position, @NonNull Add_Crops_Infromation_Model model) {

//        holder.tv_CropNameEng.setText(model.getHeaderTitleEng());
//        holder.tv_CropNameSin.setText(model.getHeaderTitleSin());
//        holder.tv_CropDetails_ENG.setText(model.getDetailTxt_ENGLISH());
//        holder.tv_CropDetails_SINDHI.setText(model.getDetailTxt_SINDHI());
//
//        Glide.with(holder.img.getContext())
//                .load(model.getImageUri())
//                .placeholder(R.drawable.ic_launcher_background)
//                .fitCenter()
//                .error(R.drawable.ic_crop)
//                .into(holder.img);
//
//        holder.tv_CropNameEng.setOnClickListener(view -> helperClass.expandable(holder.relativeLayout, holder.tv_CropDetails_ENG, holder.tv_CropDetails_SINDHI, holder.tv_CropNameEng, holder.tv_CropNameSin));
//        holder.tv_CropNameSin.setOnClickListener(view -> {
//            holder.tv_CropNameSin.setBackgroundColor(Color.parseColor("#0000FF"));
//            holder.tv_CropNameEng.setBackgroundColor(Color.parseColor("#43A047"));
//            helperClass.expandable(holder.relativeLayout, holder.tv_CropDetails_SINDHI, holder.tv_CropDetails_ENG, holder.tv_CropNameSin, holder.tv_CropNameEng);
//        });
//
//
//        holder.btnDelete.setOnClickListener(view -> {
//            AlertDialog.Builder bu = new AlertDialog.Builder(holder.btnDelete.getContext());
//            bu.setTitle("Are you sure?");
//            bu.setMessage("Deleted Data Can't be Undo");
//
//
//            bu.setPositiveButton("Delete", (dialogInterface, i) -> {
//                FirebaseDatabase.getInstance().getReference().child(new Home_Screen().All_Crop_Refernece).child(Objects.requireNonNull(getRef(position).getKey())).removeValue();
//                helperClass.DeletetingItem(model.getImageUri());
//            });
//            bu.setNegativeButton("Cancel", (dialogInterface, i) -> {
//
//            });
//            bu.show();
//
//        });

    }

    class myViewHelper extends RecyclerView.ViewHolder {
        TextView tv_CropNameEng, tv_CropNameSin, tv_CropDetails_ENG, tv_CropDetails_SINDHI, btnDelete;
        ImageView img;
        RelativeLayout relativeLayout;

        public myViewHelper(@NonNull View itemView) {
            super(itemView);
//            tv_CropNameEng = itemView.findViewById(R.id.single_crops_detail_cropNameEng);
//            tv_CropNameSin = itemView.findViewById(R.id.single_crops_detail_cropNameSin);
//            tv_CropDetails_ENG = itemView.findViewById(R.id.single_crops_eng_DetailTxt);
//            tv_CropDetails_SINDHI = itemView.findViewById(R.id.single_crops_sindhi_DetailTxt2);
//            img = itemView.findViewById(R.id.single_crops_details_Image);
//            relativeLayout = itemView.findViewById(R.id.single_crops_details_ExpandableRelativeLayout);
//            relativeLayout.setVisibility(View.GONE);
//
//
//            btnDelete = itemView.findViewById(R.id.single_crops_detail_btnDelete);
//            btnDelete.setVisibility(View.VISIBLE);
//
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//
//            usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        if (dataSnapshot.hasChild("email")) {
//                            String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
//                            helperClass.SettingBtnDelete(btnDelete, email, btnDelete.getContext());
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
        }
    }
}