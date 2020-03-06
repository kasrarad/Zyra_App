package com.example.zyra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    protected ImageView imageViewLogo;
    protected Button buttonPlantList;
    protected Button buttonSettings;
    protected Button buttonAboutUs;
    protected Button buttonCredits;
    protected Button blueToothActivityButton;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");

        setupUI();
        setButtons();

        // store the value(user's id) in the SharedPreferences
        SharedPreferences preferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        final String usersId = "1";
        editor.putString("userID", usersId);
        editor.apply();
    }


    @Override
    protected void onStart() {
        super.onStart();
        }

    public void setupUI(){
        imageViewLogo = findViewById(R.id.imageViewLogo);
    }

    public void setButtons(){
        buttonPlantList = findViewById(R.id.btnPlantList);
        buttonSettings = findViewById(R.id.btnSettings);
        buttonAboutUs = findViewById(R.id.btnAboutUs);
        buttonCredits = findViewById(R.id.btnCredits);

        blueToothActivityButton = findViewById(R.id.bluetoothActivityButton);

        buttonPlantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlantList();
            }
        });

        buttonCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCredits();
            }
        });

        buttonAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAboutUs();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });

        blueToothActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBlueToothTest();
            }
        });
    }

    public void goToSettings(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToPlantList(){
        Intent intent = new Intent(MainActivity.this, PlantActivity.class);
        startActivity(intent);
    }

    public void goToCredits(){
        Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
        startActivity(intent);
    }

    public void goToAboutUs(){
        Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void goToBlueToothTest(){
        Intent intent = new Intent(MainActivity.this, BlueToothTestActivity.class);
        startActivity(intent);
    }


}
