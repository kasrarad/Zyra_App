package com.example.zyra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zyra.Database.DeletePlants;
import com.example.zyra.Database.EditPlants;

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

public class EditPlantActivity extends AppCompatActivity {

    protected String[] plantInfo = new String[8];

    // Edit Plants
    private EditText editTextEditPlant;
    //EditText nameEditText, nameByUserEditText, temperatureEditText, moistureEditText, imageEditText, wikiEditText;
    String id, userID, nameBySpecies, nameByUser, temperature, moisture, image, wiki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Edit Plants
        editTextEditPlant = (EditText) findViewById(R.id.editTextEditPlant);
        //nameEditText = (EditText) findViewById(R.id.nameEditText);
        //temperatureEditText = (EditText) findViewById(R.id.temperatureEditText);
        //moistureEditText = (EditText) findViewById(R.id.moistureEditText);

        // get plant's name
        String plantName = getIntent().getStringExtra("nameByUser");

        // get course id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        //Connect to the database and get data
        new GetPlantInfo().execute(userID, plantName);

    }

    //Get Plants Info
    class GetPlantInfo extends AsyncTask<String, Void, String> {

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
                //JSONObject jasonResult = new JSONObject(result);


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
                        plantInfo[0] = String.valueOf(id);
                        plantInfo[1] = userID;
                        plantInfo[2] = nameBySpecies;
                        plantInfo[3] = nameByUser;
                        editTextEditPlant.setText(plantInfo[3]);
                        plantInfo[4] = temperature;
                        plantInfo[5] = moisture;
                        plantInfo[6] = image;
                        plantInfo[7] = wiki;
                    }
                } else {
                    Toast.makeText(EditPlantActivity.this, "No plants", Toast.LENGTH_SHORT).show();
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
    public void editPlantButton(View view) {
        id = plantInfo[0];
        userID = plantInfo[1];
        nameBySpecies = plantInfo[2];
        //nameByUser = plantInfo[3];
        nameByUser = editTextEditPlant.getText().toString();
        temperature = plantInfo[4];
        moisture = plantInfo[5];
        image = plantInfo[6];
        wiki = plantInfo[7];

        EditPlants editPlants = new EditPlants(this);
        editPlants.execute(id, userID, nameBySpecies, nameByUser, temperature, moisture, image, wiki);


    }

    // Delete Plant from the database
    public void deletePlantButton(View view) {
        id = plantInfo[0];

        DeletePlants deletePlants = new DeletePlants(this);
        deletePlants.execute(id);

        Intent intent = new Intent(this, PlantActivity.class);
        startActivity(intent);
    }

}
