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

        //this to set delegate/listener back to this class
        //getPlantInfo.delegate = this;

        textPlantList = findViewById(R.id.textViewPlantList);
        plantsNameListView = findViewById(R.id.plantsNameListView);
        imgAddPlant = findViewById(R.id.imageButtonAdd);
        imgBT = findViewById(R.id.imageBT);

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
/*
    public boolean checkNetworkConnection(){

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());

    }

 */
/*
    //Get Plants Info
    class GetPlantInfo extends AsyncTask<String, Void, String> {

        public AsyncResponse1 delegate = null;

        @Override
        protected String doInBackground(String... params) {

            // Define URL
            String plant_url;

            String result = "";

            // Define URL
            plant_url = "http://zyraproject.ca/selectplant.php";

            try {

                // Extract the values
                String userID = params[0];

                URL url = new URL(plant_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create data URL that we want to post
                String post_data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                // Write post data to the BufferWriter
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read the response from post request
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                resultNew = result;
                //Parsing jason Data
                try {
                    allPlants = new ArrayList<>();
                    plantNames = new ArrayList<>();
                    plantSpecies = new ArrayList<>();
                    plantPrevMoi = new ArrayList<>();
                    plantImage = new ArrayList<>();
                    //plantsNameListView = (ListView) findViewById(R.id.plantsNameListView);
                    JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

                    int success = Integer.parseInt(jasonResult.getString("success"));
                    if (success == 1) {
                        JSONArray plants = jasonResult.getJSONArray("plants");
                        plantListSize = plants.length();
                        for (int i = 0; i < plants.length(); i++) {
                            JSONObject plant = plants.getJSONObject(i);
                            int id = plant.getInt("id");
                            userID = plant.getString("userID");
                            nameBySpecies = plant.getString("nameBySpecies");
                            nameByUser = plant.getString("nameByUser");
                            temperature = plant.getString("temperature");
                            moisture = plant.getString("moisture");
                            previousMoisturesLevel = plant.getString("previousMoisturesLevel");
                            image = plant.getString("image");
                            wiki = plant.getString("wiki");

                            //line is what will be displayed on screen
                            String line = nameByUser + "\n" + moisture + "% Moisture";
                            allPlants.add(line);
                            plantNames.add(nameByUser);
                            plantSpecies.add(nameBySpecies);
                            plantPrevMoi.add(previousMoisturesLevel);
                            plantImage.add(image);
                        }

                        delegate.processFinish(allPlants);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error ", e.getMessage());
                }

            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

 */
/*
    protected void goToPlantInfoActivity() {
        Intent intent = new Intent(PlantActivity.this, PlantInfoActivity.class);
        startActivity(intent);
    }
 */
/*
    protected void refreshData() throws JSONException {

        randomNumber(moistureData);
        //Used for previous Data (Knows where to put current reading)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            time = LocalTime.now().getHour();
        }

        addCurrentMoisture(moistureData, time);


    }
 */
/*
    protected LinkedList randomNumber(LinkedList number){

        Random random = new Random();
        number.clear();
        for(int i = 0; i < plantListSize; i++ ){
            number.add(random.nextInt(100));
        }
        Iterator<Integer> iterator=number.iterator();
        while(iterator.hasNext()){
            System.out.println("Random Number: "+ iterator.next());
        }


        return number;
    }
 */
/*
    protected void addCurrentMoisture(LinkedList currentMoisture, int hour) throws JSONException {

//        fakeTime = fakeTime + 2;
//        if (fakeTime >= 24){
//            fakeTime = 0;
//        }
//        hour = fakeTime;

        if(hour >= 24){
            hour = 0;
        }


        if(hour % 2 != 0){
//            System.out.println("odd? ");
            hour--;
        }


        JSONObject jasonResult = new JSONObject(resultNew.substring(resultNew.indexOf("{"), resultNew.lastIndexOf("}") + 1));
        allPlants = new ArrayList<>();
        plantNames = new ArrayList<>();
        plantSpecies = new ArrayList<>();
        plantPrevMoi = new ArrayList<>();


        int success = Integer.parseInt(jasonResult.getString("success"));
        if (success == 1) {
            JSONArray plants = jasonResult.getJSONArray("plants");
            for (int i = 0; i < plantListSize ; i++) {


                JSONObject plant = plants.getJSONObject(i);
                EditMoisture editMoisture = new EditMoisture(this);

                String previousString = "";
                id = String.valueOf(plant.getInt("id"));
                userID = plant.getString("userID");
                nameBySpecies = plant.getString("nameBySpecies");
                nameByUser = plant.getString("nameByUser");
                temperature = plant.getString("temperature");
                moisture = plant.getString("moisture");
                previousMoisturesLevel = plant.getString("previousMoisturesLevel");
                image = plant.getString("image");
                wiki = plant.getString("wiki");


                //Prevent any OOB values
                if((int) currentMoisture.get(i) >= 100){
                    currentMoisture.set(i, 100);
                }

                //change current moisture value
                moisture = currentMoisture.get(i).toString();

                //needs to be 2 chars
                if((int) currentMoisture.get(i) >= 100){

                    currentMoisture.set(i, 99);
                }
                //needs to be 2 chars
                if( (int) currentMoisture.get(i) < 10){
                    previousString = "0";
                }

                //fix it if the default value is still wrong
                System.out.println("Char at index 0: " + previousMoisturesLevel.charAt(i));
                if(previousMoisturesLevel.charAt(0) == '['){

                    System.out.println("replace this char");
                    for(int j = 0 ; j < previousMoisturesLevel.length() ; j++){

                        previousMoisturesLevel ="000000000000000000000000";
                    }
                }
                //is now 2 chars
                previousString += currentMoisture.get(i);

                //Change the string at value (hour) and (Hour + 1)
                //We retrieve these values when creating the graph
                StringBuilder replacePrev = new StringBuilder(previousMoisturesLevel);
                replacePrev.setCharAt(hour, previousString.charAt(0));
                replacePrev.setCharAt((hour + 1),previousString.charAt(1));

                previousMoisturesLevel = replacePrev.toString();

                editMoisture.execute(id, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);
                String line = nameByUser + "\n" + moisture + "% Moisture";
                allPlants.add(line);
                plantNames.add(nameByUser);
                plantSpecies.add(nameBySpecies);
                plantPrevMoi.add(previousMoisturesLevel);



                System.out.println("Time: " + hour);
                System.out.println("\nPlant Name: " + nameByUser);
                System.out.println("Plant Species: " + nameBySpecies);
                System.out.println("rng: " + moisture);
                System.out.println("Plant previous MOIStures: " + previousMoisturesLevel);


            }
        }



        //Update data on the page
//        if (plantListSize > 0) {
//            adapter = new PlantListViewAdapter(PlantActivity.this, allPlants);
//            plantsNameListView.setAdapter(adapter);
//            adapter = new PlantListViewAdapter(PlantActivity.this ,plantNames, plantSpecies, plantPrevMoi);
//        }

    }
*/

}