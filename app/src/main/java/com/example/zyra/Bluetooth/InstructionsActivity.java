package com.example.zyra.Bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.PlantActivity;
import com.example.zyra.R;


public class InstructionsActivity extends AppCompatActivity {

    protected TextView textInstructions;
    protected TextView textViewStepOne;
    protected TextView textViewStepTwo;
    protected TextView textViewStepThree;
    protected TextView textViewStepFour;
    protected TextView textViewStepFive;
    protected Button buttonSettings;
    protected Button buttonPlant;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        setupUI();

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBluetoothSettings();
            }
        });

        buttonPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlantList();
            }
        });

    }


    public void setupUI() {
        textInstructions = findViewById(R.id.textInstructions);
        textViewStepOne = findViewById(R.id.textStep1);
        textViewStepTwo = findViewById(R.id.textStep2);
        textViewStepThree = findViewById(R.id.textStep3);
        textViewStepFour = findViewById(R.id.textStep4);
        textViewStepFive = findViewById(R.id.textStep5);

        buttonSettings = findViewById(R.id.btnSettings);
        buttonPlant = findViewById(R.id.btnPlant);
    }

    public void goToBluetoothSettings() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    public void goToPlantList() {
        Intent intent = new Intent(InstructionsActivity.this, PlantActivity.class);
        startActivity(intent);
    }
}
