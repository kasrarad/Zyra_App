package com.example.zyra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    protected TextView textPlant;
    protected ListView listViewPlants;
    protected Button btnAddPlant;
    protected Button btnEditPlant;
    protected Button btnWikiPlant;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started");

        setupUI();

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

    public void setupUI(){
        textPlant = findViewById(R.id.textViewPlant);
//        listViewPlants = findViewById(R.id.listViewPlants);
        btnAddPlant = findViewById(R.id.btnAddPlant);
        btnEditPlant = findViewById(R.id.btnModifyPlant);
        btnWikiPlant = findViewById(R.id.btnWiki);

    }

    public void goToNewPlantActivity(){
        Intent intent = new Intent(MainActivity.this, NewPlantActivity.class);
        startActivity(intent);
    }

    public void goToEditPlantProfile(){

    }

    public void goToPlantWiki(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Houseplant_care"));
        startActivity(intent);
    }
}
