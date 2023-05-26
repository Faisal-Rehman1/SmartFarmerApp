package com.Smart_Farmer.smartfarmer.Login_Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Smart_Farmer.smartfarmer.Home_Screen;
import com.Smart_Farmer.smartfarmer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login_Activity extends AppCompatActivity {

    private EditText UserEmail, UserPassword;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        TextView needNewAccountLink = findViewById(R.id.login_btnSignUp);
        UserEmail = findViewById(R.id.et_login_et_useremail);
        UserPassword = findViewById(R.id.et_login_et_userpass);
        Button loginButton = findViewById(R.id.login_btnSignIn);

        loadingBar = new ProgressDialog(this);

        needNewAccountLink.setOnClickListener(view -> SendUserToRegisterActivity());

        loginButton.setOnClickListener(view -> AllowingUserToLogin());

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }

    private void AllowingUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we are allowing you to login into your Account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity();

                            Toast.makeText(Login_Activity.this, "Logged In successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(Login_Activity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    });
        }
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(Login_Activity.this, Home_Screen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(Login_Activity.this, Registration_Activity.class);
        startActivity(registerIntent);
    }
}