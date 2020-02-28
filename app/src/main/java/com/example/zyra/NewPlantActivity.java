package com.example.zyra;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class NewPlantActivity extends AppCompatActivity {

    protected Spinner myPlantSpinner;
    protected Spinner myFrequencySpinner;
    protected EditText editPlantName;
    protected TextView textViewPlantType;
    ArrayList<Items> itemsList;
    ArrayAdapter<String> myPlantAdapter;
    ArrayAdapter<String> myFrequencyAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String TAG = "NewPlantActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplant);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Add a new plant");

        setupUI();

        //setting up the spinner that has an array list
        myPlantAdapter = new ArrayAdapter<>(NewPlantActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.plants));

        myPlantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myPlantSpinner.setAdapter(myPlantAdapter);

        //setting up frequency spinner
        myFrequencyAdapter = new ArrayAdapter<>(NewPlantActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.frequency));

        myFrequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myFrequencySpinner.setAdapter(myFrequencyAdapter);

        //setting up recycler view
        itemsList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerAdapter = new ItemsAdapter(itemsList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);

    }

    public void setupUI(){
        myPlantSpinner = findViewById(R.id.spinnerPlant);
        myFrequencySpinner = findViewById(R.id.spinnerFrequency);
        editPlantName = findViewById(R.id.editTextNewPlant);
        textViewPlantType = findViewById(R.id.textViewTypePlant);
        mRecyclerView = findViewById(R.id.recyclerViewSensor);
        mRecyclerView.setHasFixedSize(true);
    }
}

