package com.example.zyra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zyra.DatabaseDummy.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected TextView textPlant;
    protected Button btnEditPlant;
    protected Button btnWikiPlant;
    protected FloatingActionButton floatingAddPlant;

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

        floatingAddPlant.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Add New Plant Clicked");
                goToNewPlantActivity();
            }
        });

        listViewPlants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intentBundle = new Intent(MainActivity.this, PlantInfoActivity.class);
                startActivity(intentBundle);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.item1:
//                break;
//
//            case R.id.itemsettings:
//                goToSettings();
//                break;
//
//            case R.id.itemlogout:
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStart() {
        super.onStart();

        }


    public void setupUI(){
        textPlant = findViewById(R.id.textViewPlant);
        btnEditPlant = findViewById(R.id.btnModifyPlant);
        btnWikiPlant = findViewById(R.id.btnWiki);
        listViewPlants = findViewById(R.id.listViewPlants);
        floatingAddPlant = findViewById(R.id.floatingActionAddPlant);
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

//    public void goToSettings(){
//        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//        startActivity(intent);
//    }

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
