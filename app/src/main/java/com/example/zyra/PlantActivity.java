package com.example.zyra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.EditMoisture;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.PlantLocalDatabase.PlantDbHelper;
import com.example.zyra.PlantsListView.PlantListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

interface AsyncResponse1 {
    void processFinish(ArrayList<String> output);
}

//public class PlantActivity extends AppCompatActivity implements AsyncResponse1 {
public class PlantActivity extends AppCompatActivity{

    private final NetworkMonitor mybroadcast = new NetworkMonitor();
    /*
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readFromLocalDatabase();
        }
    };

     */

    protected ImageButton imgAddPlant;
//    protected Button refreshButton;
    protected ImageButton imgBT;
    protected TextView textPlantList;
    protected TextView textAddPlant;
    protected TextView textColorIndicator;

    //GetPlantInfo getPlantInfo = new GetPlantInfo();
    protected PlantDbHelper plantDbHelper;

    protected ListView plantsNameListView;
    protected PlantListViewAdapter adapter;
    protected ArrayList<String> allPlants;
    protected ArrayList<Integer> plantsID;
    protected ArrayList<String> plantNames;
    protected ArrayList<String> plantSpecies;
    protected ArrayList<String> plantPrevMoi;
    protected ArrayList<String> plantImage;
    protected ArrayList<Integer> plantSyncStatus;
    LinkedList<Integer> moistureData=new LinkedList<Integer>();
    int plantListSize;
    int time = 0;
    String id;
    String userID;
    String nameBySpecies;
    String nameByUser;
    String temperature;
    String moisture;
    String previousMoisturesLevel;
    String image;
    String wiki;
    String resultNew;
    protected String[] plantInfo = new String[9];
    static int fakeTime = 0;

//    protected Button btnPlantInfoTest;

//    protected ArrayAdapter<String> plantAdapter;

//    protected String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantlist);


        plantDbHelper = new PlantDbHelper(this);
      
        getSupportActionBar().setTitle("To Home");


        //this to set delegate/listener back to this class
        //getPlantInfo.delegate = this;

        textPlantList = findViewById(R.id.textViewPlantList);
        plantsNameListView = findViewById(R.id.plantsNameListView);
        imgAddPlant = findViewById(R.id.imageButtonAdd);
        imgBT = findViewById(R.id.imageBT);
        textAddPlant = findViewById(R.id.textViewAddPlant);
        textColorIndicator = findViewById(R.id.textViewLegend);

        String text = "Color Indicator";
        SpannableString ss = new SpannableString(text);

        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textColorIndicator.setText(content);

//        refreshButton.setEnabled(false);
        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        readFromLocalDatabase();
/*
        if(checkNetworkConnection()){
            //new GetPlantInfo().execute(userID);
            getPlantInfo.execute(userID);
        } else{
            readFromLocalDatabase();
        }

 */

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        refreshButton.setEnabled(true);
        imgAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewPlantActivity();
            }
        });

//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refreshButton.setEnabled(false);
//                try {
//                    ViewGroup layout = (ViewGroup) refreshButton.getParent();
//                    layout.removeView(refreshButton);
//                    refreshData();
//                    finish();
//                    overridePendingTransition(0, 0);
//                    startActivity(getIntent());
//                    overridePendingTransition(0, 0);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                refreshButton.setEnabled(true);
//
//            }
//        });


        /*
        btnPlantInfoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlantInfoActivity();
            }
        });
         */


//        plantsNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intentBundle = new Intent(PlantActivity.this, PlantInfoActivity.class);
//                Bundle selectedPlantBundle = new Bundle();
//                selectedPlantBundle.putLong("selectedPlant", id);
//                intentBundle.putExtras(selectedPlantBundle);
//                startActivity(intentBundle);
//            }
//        });

    }

    public void onResume() {
        super.onResume();
/*
        IntentFilter intentFilter = new IntentFilter(PlantConfig.UI_UPDATE_BROADCAST);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

 */
        //plantDbHelper = new PlantDbHelper(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mybroadcast, intentFilter);

        //readFromLocalDatabase();

/*
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mybroadcast, filter);

 */
    }

    public void onPause() {
        super.onPause();

        unregisterReceiver(mybroadcast);
    }

    public void goToNewPlantActivity(){
        Intent intent = new Intent(PlantActivity.this, NewPlantActivity.class);
        startActivity(intent);
    }
/*
    @Override
    public void processFinish(ArrayList<String> output) {
        if (output.size() > 0) {
            adapter = new PlantListViewAdapter(PlantActivity.this, output);
            plantsNameListView.setAdapter(adapter);
            adapter = new PlantListViewAdapter(PlantActivity.this ,plantNames, plantSpecies , plantPrevMoi, plantImage);
        } else {
            Toast.makeText(PlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
        }
    }
 */

    private void readFromLocalDatabase(){

        List<PlantInfoDB> plants = plantDbHelper.readFromLocalDatabase();

        allPlants = new ArrayList<>();
        plantsID = new ArrayList<>();
        plantNames = new ArrayList<>();
        plantSpecies = new ArrayList<>();
        plantPrevMoi = new ArrayList<>();
        plantImage = new ArrayList<>();
        plantSyncStatus = new ArrayList<>();

        for (int i=0; i<plants.size(); i++){
            //line is what will be displayed on screen
            String line = plants.get(i).getNameByUser() + "\n" + plants.get(i).getMoisture() + "% Moisture";
            allPlants.add(line);
            plantsID.add(plants.get(i).getID());
            plantNames.add(plants.get(i).getNameByUser());
            plantSpecies.add(plants.get(i).getNameBySpecies());
            plantPrevMoi.add(plants.get(i).getPreviousMoisturesLevel());
            plantImage.add(plants.get(i).getImage());
            plantSyncStatus.add(plants.get(i).getSyncstatus());

        }

        // Display information on the List View
        if (allPlants.size() > 0) {
           // adapter = new PlantListViewAdapter(PlantActivity.this, allPlants);
            //plantsNameListView.setAdapter(adapter);
            adapter = new PlantListViewAdapter(PlantActivity.this ,allPlants, plantsID, plantNames, plantSpecies , plantPrevMoi, plantImage, plantSyncStatus);
            plantsNameListView.setAdapter(adapter);
        } else {
            Toast.makeText(PlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
        }

        plantDbHelper.close();

    }
}