package com.example.zyra;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class NewPlantActivity extends AppCompatActivity {

    protected Spinner mySpinner;
    protected EditText editPlantName;
    protected TextView textViewPlantType;

    private static final String TAG = "NewPlantActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplant);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Add a new plant");

        setupUI();

        //setting up the spinner that has an array list
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(NewPlantActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.plants));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

    }

    public void setupUI(){
        mySpinner = findViewById(R.id.spinnerPlant);
        editPlantName = findViewById(R.id.editTextNewPlant);
        textViewPlantType = findViewById(R.id.textViewTypePlant);
    }
}

