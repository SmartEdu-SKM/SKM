package com.example.dell.smartedu;

import android.app.Application;

import com.parse.Parse;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), "5pPTGNabAK5TyJDfxKMuhzATUnMXS3GvjOS98IGD", "TRPqa2TRC5JmF2NUJLKvcdlH7j9c4saF4TODVwlG");
    }


}
