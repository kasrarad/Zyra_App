package com.example.zyra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.PlantsListView.PlantListViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class PlantActivity extends AppCompatActivity {

    protected ListView plantsNameListView;
    protected PlantListViewAdapter adapter;
    protected ArrayList<String> allPlants;
    protected FloatingActionButton floatingPlant;

    protected Button btnPlantInfoTest;

    protected ArrayAdapter<String> plantAdapter;


    protected String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantlist);

        btnPlantInfoTest = findViewById(R.id.buttonTest);
        floatingPlant = findViewById(R.id.floatingAddPlant);
        plantsNameListView = findViewById(R.id.plantsNameListView);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        new GetPlantInfo().execute(userID);

        floatingPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewPlantActivity();
            }
        });


        btnPlantInfoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlantInfoActivity();
            }
        });

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

    //Get Plants Info
    class GetPlantInfo extends AsyncTask<String, Void, String> {

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
            //Parsing jason Data
            try {
                allPlants = new ArrayList<>();
                plantsNameListView = (ListView) findViewById(R.id.plantsNameListView);
                JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1 ));

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
                        String image = plant.getString("image");
                        String wiki = plant.getString("wiki");
                        String line = nameByUser;
                        allPlants.add(line);
                    }

                    if(allPlants.size() > 0){
                        adapter = new PlantListViewAdapter(PlantActivity.this, allPlants);
                        plantsNameListView.setAdapter(adapter);
                    } else{
                        Toast.makeText(PlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
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

    protected void goToPlantInfoActivity() {
        Intent intent = new Intent(PlantActivity.this, PlantInfoActivity.class);
        startActivity(intent);
    }

}
