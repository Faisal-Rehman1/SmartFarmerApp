package com.Smart_Farmer.smartfarmer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.Smart_Farmer.smartfarmer.Login_Registration.Login_Activity;

public class MainActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        handler.postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                    finish();
                },
                4000);
    }
}