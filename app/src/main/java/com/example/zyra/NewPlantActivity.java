package com.example.zyra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.AddPlants;

public class NewPlantActivity extends AppCompatActivity {

    protected EditText editPlantName;
    protected Button btnCancelPlant;

    protected MoistureData moistureData = new MoistureData();


    // Add Plants
    //EditText nameEditText, nameByUserEditText, temperatureEditText, moistureEditText, imageEditText, wikiEditText;
    String userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki;

    private static final String TAG = "NewPlantActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplant);
        Log.d(TAG, "onCreate: Started");

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        getSupportActionBar().setTitle("Add a new plant");

        setupUI();

        btnCancelPlant.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goBackPlantList();
            }
        });

    }

    public void setupUI(){

        editPlantName = findViewById(R.id.editTextNewPlant);
        btnCancelPlant = findViewById(R.id.btnCancelPlant);

    }

    public void goBackPlantList() {
        Intent intent = new Intent(NewPlantActivity.this, PlantActivity.class);
        startActivity(intent);
    }

    // Save Plants Button
    // Add plants to the database
    public void savePlantsButton(View view) {
        //nameBySpecies = nameEditText.getText().toString();
        nameBySpecies = "";
        nameByUser = editPlantName.getText().toString().trim();
        //temperature = temperatureEditText.getText().toString();
        temperature = "";

        // Add dummy values to Moisture data
        moistureData.addMoistureData();
        moisture = moistureData.getCurrentMoistureLevel();
        previousMoisturesLevel = moistureData.getPreviousMoistureLevels();

        image = "";
        wiki = "";

        String type = "Add";

        AddPlants addPlants = new AddPlants(this);
        addPlants.execute(type, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);

        Intent intent = new Intent(this, PlantActivity.class);
        startActivity(intent);
    }
}



