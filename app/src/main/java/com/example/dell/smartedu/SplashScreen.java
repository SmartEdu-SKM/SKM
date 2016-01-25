package com.example.dell.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Parse.enableLocalDatastore(getApplicationContext());
        //Parse.initialize(getApplicationContext(), "5pPTGNabAK5TyJDfxKMuhzATUnMXS3GvjOS98IGD", "TRPqa2TRC5JmF2NUJLKvcdlH7j9c4saF4TODVwlG");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this,SignUp.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
