package com.example.zyra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.EditMoisture;
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
import java.util.Random;

interface AsyncResponse1 {
    void processFinish(ArrayList<String> output);
}

public class PlantActivity extends AppCompatActivity implements AsyncResponse1 {

    GetPlantInfo getPlantInfo = new GetPlantInfo();

    protected ListView plantsNameListView;
    protected PlantListViewAdapter adapter;
    protected ArrayList<String> allPlants;
    protected ImageButton imgAddPlant;
    protected Button refreshButton;
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

//    protected Button btnPlantInfoTest;

//    protected ArrayAdapter<String> plantAdapter;

//    protected String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantlist);

        //this to set delegate/listener back to this class
        getPlantInfo.delegate = this;

        plantsNameListView = findViewById(R.id.plantsNameListView);
        refreshButton = findViewById(R.id.buttonRefresh);
        imgAddPlant = findViewById(R.id.imageButtonAdd);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        //new GetPlantInfo().execute(userID);
        getPlantInfo.execute(userID);

        imgAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewPlantActivity();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    refreshData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void goToNewPlantActivity(){
        Intent intent = new Intent(PlantActivity.this, NewPlantActivity.class);
        startActivity(intent);
    }
    public void goToRefreshActivity(){
        Intent intent = new Intent(PlantActivity.this, PlantActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(ArrayList<String> output) {
        if (output.size() > 0) {
            adapter = new PlantListViewAdapter(PlantActivity.this, output);
            plantsNameListView.setAdapter(adapter);
        } else {
            Toast.makeText(PlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
        }
    }

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
                            String line = nameByUser + "\n" + moisture;
                            allPlants.add(line);
                        }

                        delegate.processFinish(allPlants);
/*
                        if (allPlants.size() > 0) {
                            adapter = new PlantListViewAdapter(PlantActivity.this, allPlants);
                            plantsNameListView.setAdapter(adapter);
                        } else {
                            Toast.makeText(PlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
                        }
 */
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

    protected void goToPlantInfoActivity() {
        Intent intent = new Intent(PlantActivity.this, PlantInfoActivity.class);
        startActivity(intent);
    }

    protected void refreshData() throws JSONException {

        randomNumber(moistureData);
        //Used for previous Data (Knows where to put current reading)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            time = LocalTime.now().getHour();
        }
        //       System.out.println("Real time: " + time);

        addCurrentMoisture(moistureData, time);


    }

    protected LinkedList randomNumber(LinkedList number){

        Random random = new Random();
        number.clear();
        for(int i = 0; i < plantListSize; i++ ){
            number.add(random.nextInt(100));
        }
        Iterator<Integer> iterator=number.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }


        return number;
    }

    protected void addCurrentMoisture(LinkedList currentMoisture, int hour) throws JSONException {



        if(hour >= 24){
            hour = 0;
        }


        if(hour % 2 != 0){
//            System.out.println("odd? ");
            hour--;
        }


        JSONObject jasonResult = new JSONObject(resultNew.substring(resultNew.indexOf("{"), resultNew.lastIndexOf("}") + 1));
        allPlants = new ArrayList<>();

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


                //Prevent any outliers
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
                //is now 2 chars
                previousString += currentMoisture.get(i);

                //Change the string at value (hour) and (Hour + 1)
                //We retrieve these values when creating the graph
                StringBuilder replacePrev = new StringBuilder(previousMoisturesLevel);
                replacePrev.setCharAt(hour, previousString.charAt(0));
                replacePrev.setCharAt((hour + 1),previousString.charAt(1));


//                System.out.println("previous String: " + previousString);


                previousMoisturesLevel = replacePrev.toString();

//                System.out.println("Previous Moisture level: "+ previousMoisturesLevel);


                // System.out.println("Previous moisture length: " + previousMoisturesLevel.toString().length() + "\nPrevious Moisture: " + previousMoisturesLevel);


                editMoisture.execute(id, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);
                String line = nameByUser + "\n" + moisture + "% Moisture";
                allPlants.add(line);

            }
        }

        //Update data on the page
        if (plantListSize > 0) {
            adapter = new PlantListViewAdapter(PlantActivity.this, allPlants);
            plantsNameListView.setAdapter(adapter);
        }



    }




}