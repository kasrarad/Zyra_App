package com.example.zyra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zyra.Database.EditMoisture;
import com.example.zyra.Database.EditPlants;
import com.example.zyra.PlantsListView.PlantListViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.util.ArrayList;
import java.util.List;

interface AsyncResponse {
    void processFinish(String[] output);
}

public class MoistureActivity extends AppCompatActivity implements AsyncResponse {

    GetPlantData getPlantData = new GetPlantData();

    protected String[] plantInfo = new String[9];

    private TextView currentMoisturelevel;
    private TextView previousMoistureTextView;
    protected ListView moistureListView;

    protected MoistureData moistureData = new MoistureData();

    String id, userID;
;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);

        //this to set delegate/listener back to this class
        getPlantData.delegate = this;

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        System.out.println("hello1:");

        // I put 2 classes
        // GetPlantInfo : Get the data from Database and show them in the ListView
        // GetPlantData : Only get the data from Database and store them in the plantInfo Array

        currentMoisturelevel = (TextView) findViewById(R.id.currentMoistureTextView);
        previousMoistureTextView = (TextView) findViewById(R.id.previousMoistureTextView);
        moistureListView = (ListView) findViewById(R.id.moistureListView);

        // get plant's name
        String plantName = getIntent().getStringExtra("nameByUser");

        // get course id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        // call GetPlantInfo class(get data and show them inside ListView
        //getPlantData.execute(userID, plantName);

        // Call GetPlantData class(only get the data)
        getPlantData.execute(userID, plantName);

        System.out.println("hello2:");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Do whatever you want inside the processFinish function

    }

    @Override
    public void processFinish(String[] plantInfo) {

        System.out.println("id: " + plantInfo[0]);
        System.out.println("userID: " + plantInfo[1]);
        System.out.println("nameBySpecies: " + plantInfo[2]);
        System.out.println("nameByUser: " + plantInfo[3]);
        System.out.println("temperature: " + plantInfo[4]);
        System.out.println("moisture: " + plantInfo[5]);
        System.out.println("previousMoisturesLevel: " + plantInfo[6]);
        System.out.println("image: " + plantInfo[7]);
        System.out.println("wiki: " + plantInfo[8]);

        // Do whatever you want here(all the information about the plant are stored in planInfo array)
        /*
        id = plantInfo[0];
        String userID = plantInfo[1];
        String nameBySpecies = plantInfo[2];
        String nameByUser = plantInfo[3];
        String temperature = plantInfo[4];
        String  moisture = plantInfo[5];
        String previousMoisturesLevel = plantInfo[6];
        String image = plantInfo[7];
        String wiki = plantInfo[8];
         */
        // You should change the plantInfo[5] and plantInfo[6]
        // As an example I added some dummy value
        // Add dummy values to Moisture data
        moistureData.addMoistureData();

        // this function convert the current Moisture(Integer value) to the String
        plantInfo[5] = moistureData.getCurrentMoistureLevel();

        // this function convert the array of data to a String
        // Make sure you have 12 values or just add null value to the previousMoistureLevels List
        plantInfo[6] = moistureData.getPreviousMoistureLevels();

        // convert back the plantInfo[6] from String to array
        // Get array data one by one
        if(plantInfo[6].equals("")){
            //Toast.makeText(MoistureActivity.this, "No Moisture Data Available", Toast.LENGTH_SHORT).show();
        } else{
            Gson gson = new Gson();
            List<Integer> oldMoisture;
            oldMoisture = gson.fromJson(plantInfo[6], new TypeToken<List<Integer>>(){}.getType());
            for(int i=0; i<12; i++){
                System.out.println(oldMoisture.get(i));
            }
        }

        // at the end, after you get the moisture data you should run the following codes to edit the database
        editPlantButton();

    }

    //Get Plants Info
    class GetPlantInfo extends AsyncTask<String, Void, String> {

        ArrayAdapter<String> adapter;

        @Override
        protected String doInBackground(String... params) {

            // Define URL
            String plant_url;

            String result = "";

            // Define URL
            plant_url = "http://zyraproject.ca/getplantinfo.php";

            try {

                // Extract the values
                String userID = params[0];
                String nameByUser = params[1];

                URL url = new URL(plant_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create data URL that we want to post
                String post_data = URLEncoder.encode("userID", "UTF-8")+"="+URLEncoder.encode(userID, "UTF-8")+"&"
                        +URLEncoder.encode("nameByUser", "UTF-8")+"="+URLEncoder.encode(nameByUser, "UTF-8");
                // Write post data to the BufferWriter
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read the response from post request
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
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
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            //Parsing jason Data
            try {

                adapter = new ArrayAdapter<>(MoistureActivity.this, android.R.layout.simple_list_item_1);

                JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
                System.out.println("moisture:");
                int success = Integer.parseInt(jasonResult.getString("success"));

                if (success == 1) {
                    JSONArray plants = jasonResult.getJSONArray("plants");
                    for (int i = 0; i < plants.length(); i++) {
                        JSONObject plant = plants.getJSONObject(i);
                        int id = plant.getInt("id");
                        String userID = plant.getString("userID");
                        String nameBySpecies = plant.getString("nameBySpecies");
                        String nameByUser = plant.getString("nameByUser");
                        String temperature = plant.getString("temperature");
                        String moisture = plant.getString("moisture");
                        String previousMoisturesLevel = plant.getString("previousMoisturesLevel");
                        String image = plant.getString("image");
                        String wiki = plant.getString("wiki");
                        plantInfo[0] = String.valueOf(id);
                        plantInfo[1] = userID;
                        plantInfo[2] = nameBySpecies;
                        plantInfo[3] = nameByUser;
                        plantInfo[4] = temperature;
                        System.out.println("moisture:" + moisture) ;
                        plantInfo[5] = moisture;
                        currentMoisturelevel.setText("Current Moisture: " + plantInfo[5]);
                        plantInfo[6] = previousMoisturesLevel;
                        plantInfo[7] = image;
                        plantInfo[8] = wiki;
                    }

                    if(plantInfo[6].equals("")){
                        Toast.makeText(MoistureActivity.this, "No Moisture Data Available", Toast.LENGTH_SHORT).show();
                    } else{
                        Gson gson = new Gson();
                        List<Integer> oldMoisture;
                        oldMoisture = gson.fromJson(plantInfo[6], new TypeToken<List<Integer>>(){}.getType());
                        for(int i=0; i<12; i++){
                            adapter.add(oldMoisture.get(i).toString());
                        }
                        moistureListView.setAdapter(adapter);
                    }

                } else {
                    Toast.makeText(MoistureActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error ", e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    //Get Plants Info
    class GetPlantData extends AsyncTask<String, Void, String> {

        public AsyncResponse delegate = null;

        @Override
        protected String doInBackground(String... params) {

            // Define URL
            String plant_url;

            String result = "";

            // Define URL
            plant_url = "http://zyraproject.ca/getplantinfo.php";

            try {

                // Extract the values
                String userID = params[0];
                String nameByUser = params[1];

                URL url = new URL(plant_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create data URL that we want to post
                String post_data = URLEncoder.encode("userID", "UTF-8")+"="+URLEncoder.encode(userID, "UTF-8")+"&"
                        +URLEncoder.encode("nameByUser", "UTF-8")+"="+URLEncoder.encode(nameByUser, "UTF-8");
                // Write post data to the BufferWriter
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read the response from post request
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
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
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            //Parsing jason Data
            try {

                JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

                int success = Integer.parseInt(jasonResult.getString("success"));
                if (success == 1) {
                    JSONArray plants = jasonResult.getJSONArray("plants");
                    for (int i = 0; i < plants.length(); i++) {
                        JSONObject plant = plants.getJSONObject(i);
                        int id = plant.getInt("id");
                        String userID = plant.getString("userID");
                        String nameBySpecies = plant.getString("nameBySpecies");
                        String nameByUser = plant.getString("nameByUser");
                        String temperature = plant.getString("temperature");
                        String moisture = plant.getString("moisture");
                        String previousMoisturesLevel = plant.getString("previousMoisturesLevel");
                        String image = plant.getString("image");
                        String wiki = plant.getString("wiki");
                        plantInfo[0] = String.valueOf(id);
                        plantInfo[1] = userID;
                        plantInfo[2] = nameBySpecies;
                        plantInfo[3] = nameByUser;
                        plantInfo[4] = temperature;
                        plantInfo[5] = moisture;
                        currentMoisturelevel.setText("Current Moisture: " + plantInfo[5]);
                        plantInfo[6] = previousMoisturesLevel;
                        plantInfo[7] = image;
                        plantInfo[8] = wiki;
                    }

                    delegate.processFinish(plantInfo);

                } else {
                    Toast.makeText(MoistureActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("error ", e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    // Edit Plant in the database
    public void editPlantButton() {
        String id = plantInfo[0];
        String userID = plantInfo[1];
        String nameBySpecies = plantInfo[2];
        String nameByUser = plantInfo[3];
        String temperature = plantInfo[4];
        String  moisture = plantInfo[5];
        String previousMoisturesLevel = plantInfo[6];
        String image = plantInfo[7];
        String wiki = plantInfo[8];

        EditMoisture editPlants = new EditMoisture(this);
        editPlants.execute(id, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);

    }

}
