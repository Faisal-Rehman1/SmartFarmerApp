package com.Smart_Farmer.smartfarmer.Login_Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.Smart_Farmer.smartfarmer.HelperClass;
import com.Smart_Farmer.smartfarmer.Home_Screen;
import com.Smart_Farmer.smartfarmer.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration_Activity extends AppCompatActivity {

    public String Registration_refernece = "Users";
    private EditText UserEmail, UserPassword, UserConfirmPassword, UserName, UserAddress;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    CircleImageView userImg;
    Uri imageUri;
    Bitmap selectedImage;
    Spinner spinner;

    String[] cat = {"Farmer", "Learner"};
    String selectedCat;


    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    final static int Gallery_Pick = 1;

    HelperClass helperClass = new HelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference(Registration_refernece);
        databaseReference = FirebaseDatabase.getInstance().getReference(Registration_refernece);


        spinner = findViewById(R.id.spinner_reg_JoiningCategory);

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


        UserEmail = findViewById(R.id.et_reg_et_useremail);
        UserPassword = findViewById(R.id.et_reg_et_userpassword);
        UserConfirmPassword = findViewById(R.id.et_reg_et_userConfrimpassword);
        UserAddress = findViewById(R.id.et_reg_et_useraddress);
        UserName = findViewById(R.id.et_reg_et_username);
        AppCompatButton createAccountButton = findViewById(R.id.reg_btnSignUP);
        TextView signin = findViewById(R.id.reg_btnSginIn);
        userImg = findViewById(R.id.reg_userPrifile);

        signin.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Login_Activity.class)));

        loadingBar = new ProgressDialog(this);
        createAccountButton.setOnClickListener(view -> CreateNewAccount(selectedImage));

        userImg.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, Gallery_Pick);
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(Registration_Activity.this, Home_Screen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                userImg.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }


    private void CreateNewAccount(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();
        String username = UserName.getText().toString();
        String userAddress = UserAddress.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write Username...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userAddress)) {
            Toast.makeText(this, "Please write your Address...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
        } else if (imageUri != null) {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + helperClass.getfileExt(imageUri, getApplicationContext()));


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {

                            reference.putBytes(data)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                                        while (!uriTask.isSuccessful()) ;
                                        String downloadUri = uriTask.getResult().toString();

                                        Registration_Model member = new Registration_Model(
                                                email,
                                                username,
                                                selectedCat,
                                                userAddress,
                                                downloadUri
                                        );


                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                        databaseReference.child(currentUserID).setValue(member);

                                        SendUserToMainActivity();
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(), "Registered Successfully!!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                            SendUserToMainActivity();
                        } else {
                            String message = Objects.requireNonNull(t.getException()).getMessage();
                            Toast.makeText(Registration_Activity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    });
        }
    }
}
