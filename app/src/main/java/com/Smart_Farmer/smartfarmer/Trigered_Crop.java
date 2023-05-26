package com.Smart_Farmer.smartfarmer;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Trigered_Crop extends AppCompatActivity {
    RecyclerView recyclerView;
    Crops_Detail_Adapter mainAdapter;
    FloatingActionButton floatingActionButton;
    AlertDialog alertDialog;
    public String Crops_detail_Refernece = "all_crops_details";

    AppCompatButton btnAddCrops;
    ProgressBar progressBar;
    EditText et_CropNameEng, et_CropNameSin, et_CropDetails_SIN, et_CropDetails_ENG;
    ImageView CropImage;
    Bitmap selectedImage;
    private Uri ImageUri;
    int Image_Request_Code = 7;
    String uid, cropTitleEng, CropTitleSind, CropProfileUri;
    CircleImageView cropImageView;
    TextView cropNameSin, cropNameEng;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    Toolbar toolbar;

    HelperClass helperClass = new HelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigered_crop);

        uid = getIntent().getStringExtra("uid");
        CropTitleSind = getIntent().getStringExtra("cropNameSin");
        cropTitleEng = getIntent().getStringExtra("cropNameEng");
        CropProfileUri = getIntent().getStringExtra("cropImageUrl");

        toolbar = findViewById(R.id.trigered_toolbar);
        cropImageView = findViewById(R.id.toolbar_image);
        cropNameSin = findViewById(R.id.toolbar_titleSin);
        cropNameEng = findViewById(R.id.toolbar_titleEng);

        cropNameSin.setText(CropTitleSind);
        cropNameEng.setText(cropTitleEng);
        Glide.with(getApplicationContext()).load(CropProfileUri).placeholder(R.drawable.ic_crop).into(cropImageView);


        floatingActionButton = findViewById(R.id.trigered_crops_detail_FAB);

        helperClass.SettingFAB(floatingActionButton, getApplicationContext());

        helperClass.SettingFAB(floatingActionButton, getApplicationContext());

        recyclerView = findViewById(R.id.trigered_crops_detail_RV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mLayoutManager.setReverseLayout(true); // THIS ALSO SETS setStackFromBottom to true
        recyclerView.setLayoutManager(mLayoutManager);

        floatingActionButton.setOnClickListener(view -> startPopUp());


        recyclerView.setAdapter(mainAdapter);
        FirebaseRecyclerOptions<Add_Crops_Infromation_Model> options =
                new FirebaseRecyclerOptions.Builder<Add_Crops_Infromation_Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child(Crops_detail_Refernece).orderByChild("uid").startAt(uid).endAt(uid + "~"), Add_Crops_Infromation_Model.class)
                        .build();

        mainAdapter = new Crops_Detail_Adapter(options);
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
        LayoutInflater inflater = LayoutInflater.from(Trigered_Crop.this);
        View v = inflater.inflate(R.layout.add_crops_detail_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Trigered_Crop.this);
        alertDialogBuilder.setView(v);

        et_CropNameEng = v.findViewById(R.id.add_cops_detail_headerTitleEng);
        et_CropNameSin = v.findViewById(R.id.add_cops_detail_headerTitleSin);
        et_CropDetails_ENG = v.findViewById(R.id.add_cops_detail_header_detailstxt_English);
        et_CropDetails_SIN = v.findViewById(R.id.add_cops_detail_header_detailstxt_Sindhi);
        btnAddCrops = v.findViewById(R.id.add_cops_detail_btnAddCropInfo);
        CropImage = v.findViewById(R.id.add_cops_detail_image);
        progressBar = v.findViewById(R.id.add_cops_detail_progressbar);

        storageReference = FirebaseStorage.getInstance().getReference(Crops_detail_Refernece);
        databaseReference = FirebaseDatabase.getInstance().getReference(Crops_detail_Refernece);

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
        String CropDetailTxt_ENG = et_CropDetails_ENG.getText().toString();
        String CropDetailTxt_SIN = et_CropDetails_SIN.getText().toString();

        if (TextUtils.isEmpty(CropNameEng)) {
            Toast.makeText(getApplicationContext(), "Please Enter Crop Name in English", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(CropNameSin)) {
            Toast.makeText(getApplicationContext(), "Please Enter Crop Name in Sindhi", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(CropDetailTxt_ENG)) {
            Toast.makeText(getApplicationContext(), "Please Enter Crop Details in ENGLISH", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(CropDetailTxt_SIN)) {
            Toast.makeText(getApplicationContext(), "Please Enter Crop Details in Sindhi", Toast.LENGTH_SHORT).show();
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
                        Add_Crops_Infromation_Model member = new Add_Crops_Infromation_Model(
                                CropNameSin,
                                CropNameEng,
                                downloadUri,
                                CropDetailTxt_ENG,
                                CropDetailTxt_SIN,
                                uid
                        );

                        String upload = databaseReference.push().getKey();
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
}