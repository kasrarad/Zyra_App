package com.example.zyra;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class Networking extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
