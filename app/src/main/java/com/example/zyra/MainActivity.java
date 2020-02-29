package com.example.zyra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zyra.DatabaseDummy.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected TextView textPlant;
    protected Button btnAddPlant;
    protected Button btnEditPlant;
    protected Button btnWikiPlant;

    private static final String TAG = "MainActivity";

    protected ListView listViewPlants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertPlant(new Plants("TitleTest", "PlantTypeTest"));

        setupUI();

        loadListView();

        btnAddPlant.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Add New Plant Clicked");
                goToNewPlantActivity();
            }
        });

        btnWikiPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Wiki");
                goToPlantWiki();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        }


    public void setupUI(){
        textPlant = findViewById(R.id.textViewPlant);
        btnAddPlant = findViewById(R.id.btnAddPlant);
        btnEditPlant = findViewById(R.id.btnModifyPlant);
        btnWikiPlant = findViewById(R.id.btnWiki);
        listViewPlants = findViewById(R.id.listViewPlants);


    }

    public void goToNewPlantActivity(){
        Intent intent = new Intent(MainActivity.this, NewPlantActivity.class);
        startActivity(intent);
    }

    public void goToEditPlantProfile(){
        Intent intent = new Intent(MainActivity.this, InsertPlantInfoDialogFragment.class);
        startActivity(intent);
    }

    public void goToPlantWiki(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Houseplant_care"));
        startActivity(intent);
    }

    protected void loadListView(){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Plants> plants = dbHelper.getAllPlants();

        ArrayList<String> plantListText = new ArrayList<>();

        for (int i = 0; i < plants.size(); i++){
            String temp = "";
            temp += plants.get(i).getPlantTitle() + "\n";
            temp += plants.get(i).getPlantType() + "\n";

            plantListText.add(temp);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, plantListText);
        listViewPlants.setAdapter(arrayAdapter);
    }
}
