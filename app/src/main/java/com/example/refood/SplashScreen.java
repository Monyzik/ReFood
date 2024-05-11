package com.example.refood;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        auth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
                if (auth.getCurrentUser() == null) {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();

                } else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
                overridePendingTransition( 0, R.anim.my_splash_fade_out);
            }
        }, 1000);
    }
}