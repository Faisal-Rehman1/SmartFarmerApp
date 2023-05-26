package com.Smart_Farmer.smartfarmer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class Home_Screen extends AppCompatActivity {

    RecyclerView recyclerView;
    CropsAdapter mainAdapter;
    FloatingActionButton floatingActionButton;
    AlertDialog alertDialog;
    public String All_Crop_Refernece = "all_crops";

    AppCompatButton btnAddCrops;
    ProgressBar progressBar;
    EditText et_CropNameEng, et_CropNameSin;
    ImageView CropImage, profileImg;
    Bitmap selectedImage;
    private Uri ImageUri;
    int Image_Request_Code = 7;
    String currentUserID;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    Toolbar toolbar;
    HelperClass helperClass = new HelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        toolbar = findViewById(R.id.home_toolbar);
        profileImg = findViewById(R.id.toolbar_image);
        setGetProfileImg();

        profileImg.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Profile.class)));


        floatingActionButton = findViewById(R.id.trigered_crops_FAB);

        helperClass.SettingFAB(floatingActionButton, getApplicationContext());

        recyclerView = findViewById(R.id.crops_rv);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mLayoutManager.setReverseLayout(true); // THIS ALSO SETS setStackFromBottom to true
        recyclerView.setLayoutManager(mLayoutManager);

        floatingActionButton.setOnClickListener(view -> startPopUp());

        recyclerView.setAdapter(mainAdapter);
        FirebaseRecyclerOptions<Add_Crops_Model> options =
                new FirebaseRecyclerOptions.Builder<Add_Crops_Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child(All_Crop_Refernece), Add_Crops_Model.class)
                        .build();

        mainAdapter = new CropsAdapter(options);
        recyclerView.setAdapter(mainAdapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        mainAdapter.startListening();

        mainAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                assert data != null;
                ImageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(ImageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);

                CropImage.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


    //method for popup
    public void startPopUp() {
        LayoutInflater inflater = LayoutInflater.from(Home_Screen.this);
        View v = inflater.inflate(R.layout.add_crops_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Home_Screen.this);
        alertDialogBuilder.setView(v);

        et_CropNameEng = v.findViewById(R.id.add_cops_headerTitleEng);
        et_CropNameSin = v.findViewById(R.id.add_cops_headerTitleSin);
        btnAddCrops = v.findViewById(R.id.add_cops_btnAddCrop);
        progressBar = v.findViewById(R.id.add_cops_progressbar);

        CropImage = v.findViewById(R.id.add_cops_image);

        storageReference = FirebaseStorage.getInstance().getReference(All_Crop_Refernece);
        databaseReference = FirebaseDatabase.getInstance().getReference(All_Crop_Refernece);

        CropImage.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, Image_Request_Code);
        });

        btnAddCrops.setOnClickListener(view -> uploadFile(selectedImage));

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void uploadFile(Bitmap bitmap) {


        String CropNameEng = et_CropNameEng.getText().toString();
        String CropNameSin = et_CropNameSin.getText().toString();

        if (TextUtils.isEmpty(CropNameEng)) {
            Toast.makeText(getApplicationContext(), "Please Enter Crop Name in English", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(CropNameSin)) {
            Toast.makeText(getApplicationContext(), "Please Enter Crop Name in Sindhi", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (ImageUri == null) {
            Toast.makeText(getApplicationContext(), "Please Chose Crop Image", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else {

            progressBar.setVisibility(View.VISIBLE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();


            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + helperClass.getfileExt(ImageUri, getApplicationContext()));
            reference.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = uriTask.getResult().toString();
                        String upload = databaseReference.push().getKey();
                        Add_Crops_Model member = new Add_Crops_Model(
                                CropNameSin,
                                CropNameEng,
                                upload,
                                downloadUri
                        );

                        assert upload != null;
                        databaseReference.child(upload).setValue(member);

                        progressBar.setVisibility(View.GONE);
                        alertDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Crop Data Uploaded!!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void setGetProfileImg() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid(); //getting current user id

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileImageUri")) {
                        String profilePic = Objects.requireNonNull(dataSnapshot.child("profileImageUri").getValue()).toString();
//                        UserDataList.add(profilePic);
                        Glide.with(getApplicationContext())
                                .load(profilePic)
                                .placeholder(R.drawable.ic_launcher_background)
                                .fitCenter()
                                .error(R.drawable.profile)
                                .into(profileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}