package com.Smart_Farmer.smartfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Smart_Farmer.smartfarmer.Login_Registration.Login_Activity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    TextView tvName, tvPhone, tvGmail, tvCat, btnDashboard, btnEditProfile, btnEditProfileImg;
    CircleImageView imgProfile, editProfile, editProfile1;
    Button btnLgout;
    AlertDialog alertDialog1, alertDialog;
    Uri imageUri;
    Bitmap selectedImage;
    private ProgressDialog loadingBar1, loadingBar;
    private StorageReference storageReference;
    private EditText UserEmail;
    private EditText UserName;
    private EditText UserAddress;

    Spinner spinner;

    String[] cat = {"Farmer", "Learner"};
    String selectedCat;

    //firebase auth
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    String currentUserID;

    HelperClass helperClass = new HelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid(); //getting current user id
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users"); //here Users is  table name


        //refernces // finding id's
        tvName = findViewById(R.id.userName);
        tvPhone = findViewById(R.id.userPhone);
        tvGmail = findViewById(R.id.userGmail);
        tvCat = findViewById(R.id.userJoinedCat);
        imgProfile = findViewById(R.id.Circle_profileImge);
        btnDashboard = findViewById(R.id.btnDashboard);
        btnLgout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btn_tv_editProfile);
        btnEditProfileImg = findViewById(R.id.btn_tv_editProfileImg);

        btnEditProfileImg.setOnClickListener(view -> startPopUpProfile());

        btnEditProfile.setOnClickListener(view -> startPopUp());

        //hiding the dashboard button
        btnDashboard.setVisibility(View.GONE);

        //getting current user to satisfy admins
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("username")) {
                        String fullname = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                        tvName.setText(fullname);
                    }
                    if (dataSnapshot.hasChild("profileImageUri")) {
                        String image = Objects.requireNonNull(dataSnapshot.child("profileImageUri").getValue()).toString();
                        Glide.with(Profile.this).load(image).placeholder(R.drawable.profile).into(imgProfile);
                    }
                    if (dataSnapshot.hasChild("userAddress")) {
                        tvPhone.setText(Objects.requireNonNull(dataSnapshot.child("userAddress").getValue()).toString());
                    }
                    if (dataSnapshot.hasChild("email")) {
                        String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                        tvGmail.setText(email);
                        helperClass.SettingBtnDelete(btnDashboard, email, Profile.this);
                    }
                    if (dataSnapshot.hasChild("joiningCat")) {
                        tvCat.setText(Objects.requireNonNull(dataSnapshot.child("joiningCat").getValue()).toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnDashboard.setOnClickListener(view -> startActivity(new Intent(Profile.this, Dashboard.class)));

        btnLgout.setOnClickListener(view -> {
            mAuth.signOut();
            SendUserToLoginActivity();
        });

        loadingBar = new ProgressDialog(this);
        loadingBar1 = new ProgressDialog(this);

    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(Profile.this, Login_Activity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    //method for popup
    @SuppressLint("SetTextI18n")
    public void startPopUp() {
        LayoutInflater inflater = LayoutInflater.from(Profile.this);
        View v = inflater.inflate(R.layout.activity_registration, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile.this);
        alertDialogBuilder.setView(v);

        editProfile1 = v.findViewById(R.id.reg_userPrifile);
        editProfile1.setVisibility(View.GONE);
        AppCompatButton signin = v.findViewById(R.id.reg_btnSginIn);
        signin.setVisibility(View.GONE);
        EditText userPassword = v.findViewById(R.id.et_reg_et_userpassword);
        EditText userConfirmPassword = v.findViewById(R.id.et_reg_et_userConfrimpassword);
        userPassword.setVisibility(View.GONE);
        userConfirmPassword.setVisibility(View.GONE);
//
        spinner = v.findViewById(R.id.spinner_reg_JoiningCategory);

        ArrayAdapter<String> ad = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item, cat
        );
        spinner.setAdapter(ad);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCat = cat[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("userAddress")) {
                        String add = Objects.requireNonNull(dataSnapshot.child("userAddress").getValue()).toString();
                        UserAddress.setText(add);
                    }
                    if (dataSnapshot.hasChild("joiningCat")) {
                        selectedCat = Objects.requireNonNull(dataSnapshot.child("joiningCat").getValue()).toString();
                    }
                    if (dataSnapshot.hasChild("username")) {
                        String name = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                        UserName.setText(name);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        UserEmail = v.findViewById(R.id.et_reg_et_useremail);
        UserEmail.setVisibility(View.GONE);
        UserAddress = v.findViewById(R.id.et_reg_et_useraddress);

        UserName = v.findViewById(R.id.et_reg_et_username);
        AppCompatButton upadateAccount = v.findViewById(R.id.reg_btnSignUP);

        upadateAccount.setText("Update Profile");

        upadateAccount.setOnClickListener(view -> UpdateProfile());


        alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
    }

    //method for popup
    public void startPopUpProfile() {
        LayoutInflater inflater = LayoutInflater.from(Profile.this);
        View v = inflater.inflate(R.layout.popup_update_profile_img, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile.this);
        alertDialogBuilder.setView(v);


        storageReference = FirebaseStorage.getInstance().getReference("Users");


        editProfile = v.findViewById(R.id.popup_reg_userPrifile);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileImageUri")) {
                        String image = Objects.requireNonNull(dataSnapshot.child("profileImageUri").getValue()).toString();
                        Glide.with(getApplicationContext()).load(image).placeholder(R.drawable.profile).into(editProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        AppCompatButton updateProfielImg = v.findViewById(R.id.popup_btnUpdateProfikeImg);

        updateProfielImg.setOnClickListener(view -> UpdateProfileImg(selectedImage));


        editProfile.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 1);
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = Profile.this.getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                editProfile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }

    private void UpdateProfileImg(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        if (imageUri != null) {
            loadingBar.setTitle("Updating Profile Image");
            loadingBar.setMessage("Please wait, while we are updating your Profile Image...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + helperClass.getfileExt(imageUri, getApplicationContext()));

            reference.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = uriTask.getResult().toString();

                        Map<String, Object> map = new HashMap<>();
                        map.put("profileImageUri", downloadUri);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//                            databaseReference.child(currentUserID).updateChildren(map);


                        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).updateChildren(map)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getApplicationContext(), "Data Update Successfully", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    alertDialog.dismiss();
                                }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Error While Updating", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            alertDialog.dismiss();
                        });
                    })
                    .addOnFailureListener(e -> {
                        loadingBar.dismiss();
                        alertDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });


        }

    }

    private void UpdateProfile() {
        String email = UserEmail.getText().toString();
        String username = UserName.getText().toString();
        String userAddress = UserAddress.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please write your email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please write Username...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userAddress)) {
            Toast.makeText(getApplicationContext(), "Please write your Address...", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar1.setTitle("Updating Account");
            loadingBar1.setMessage("Please wait, while we are updating your Account...");
            loadingBar1.show();
            loadingBar1.setCanceledOnTouchOutside(true);

            Map<String, Object> map = new HashMap<>();
            map.put("userAddress", userAddress);
            map.put("joiningCat", selectedCat);
            map.put("username", username);


            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).updateChildren(map)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Data Update Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar1.dismiss();
                        alertDialog1.dismiss();
                    }).addOnFailureListener(
                    e -> {
                        Toast.makeText(getApplicationContext(), "Error While Updating", Toast.LENGTH_SHORT).show();
                        loadingBar1.dismiss();
                        alertDialog1.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        loadingBar1.dismiss();
                        alertDialog1.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    }
}