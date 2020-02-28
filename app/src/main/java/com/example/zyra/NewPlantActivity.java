package com.example.zyra;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    ArrayList<Items> mItemsList;
    ArrayAdapter<String> myPlantAdapter;
    ArrayAdapter<String> myFrequencyAdapter;

    private RecyclerView mRecyclerView;
    private ItemsAdapter mItemsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button btnInsert;
    private Button btnDelete;
    private EditText editTextInsert;
    private EditText editTextDelete;

    private static final String TAG = "NewPlantActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplant);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Add a new plant");

        setupUI();

        createItemsList();
        buildRecyclerView();

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

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertSensor(position);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextDelete.getText().toString());
                deleteSensor(position);
            }
        });

        mItemsAdapter.setOnItemClickListener(new ItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mItemsList.get(position).changeSensorA("Clicked");
                mItemsAdapter.notifyItemChanged(position);
            }
        });
    }

    public void setupUI(){
        myPlantSpinner = findViewById(R.id.spinnerPlant);
        myFrequencySpinner = findViewById(R.id.spinnerFrequency);
        editPlantName = findViewById(R.id.editTextNewPlant);
        textViewPlantType = findViewById(R.id.textViewTypePlant);
        mRecyclerView = findViewById(R.id.recyclerViewSensor);
        mRecyclerView.setHasFixedSize(true);
        btnInsert = findViewById(R.id.btnInsertSensor);
        btnDelete = findViewById(R.id.btnDeleteSensor);
        editTextInsert = findViewById(R.id.editTextInsertSensor);
        editTextDelete = findViewById(R.id.editTextDeleteSensor);
    }

    public void createItemsList(){
        //setting up recycler view
        mItemsList = new ArrayList<>();
        mItemsList.add(new Items("Sensor A"));
    }
        public void buildRecyclerView(){
        mLayoutManager = new LinearLayoutManager(this);
        mItemsAdapter = new ItemsAdapter(mItemsList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mItemsAdapter);
    }

    public void insertSensor(int position){
        mItemsList.add(position, new Items("New Item At Position" + position));
        mItemsAdapter.notifyItemInserted(position);
    }

    public void deleteSensor(int position){
        mItemsList.remove(position);
        mItemsAdapter.notifyItemRemoved(position);
    }
}

