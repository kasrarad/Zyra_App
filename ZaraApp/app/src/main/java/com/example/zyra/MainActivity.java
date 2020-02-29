package com.example.zyra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class MainActivity extends AppCompatActivity {

    protected ArrayAdapter<String> adapter;
    protected ArrayList<String> allPlants;

    protected TextView textPlant;
    protected ListView plantsNameListView;
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

        btnEditPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Edit Plant Clicked");
                goToEditPlantActivity();
            }
        });

        // Display plants in the List View
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        plantsNameListView.setAdapter(adapter);
        final String userID = "1";
        new GetPlantInfo().execute(userID);

        // By clicking on each plant, we go to EditPlant activity
        // sent the plant's name and user's id to the assignment activity
        plantsNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent usersProfile = new Intent(MainActivity.this, EditPlantActivity.class);

                // store the value(plant's name and user's id) in the SharedPreferences
                SharedPreferences preferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                final String usersId = userID;
                editor.putString("userID", usersId);
                editor.putString("nameByUser", allPlants.get(position));
                editor.apply();

                //startActivity(usersProfile);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void setupUI(){
        textPlant = findViewById(R.id.textViewPlant);
        plantsNameListView = findViewById(R.id.plantsNameListView);
        btnAddPlant = findViewById(R.id.btnAddPlant);
        btnEditPlant = findViewById(R.id.btnModifyPlant);
        btnWikiPlant = findViewById(R.id.btnWiki);

    }

    public void goToNewPlantActivity(){
        Intent intent = new Intent(MainActivity.this, NewPlantActivity.class);
        startActivity(intent);
    }

    public void goToPlantWiki(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Houseplant_care"));
        startActivity(intent);
    }

    private void goToEditPlantActivity() {
        Intent intent = new Intent(MainActivity.this, EditPlantActivity.class);
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
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String result) {
            //Parsing jason Data
            try {
                allPlants = new ArrayList<>();
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
                        String image = plant.getString("image");
                        String wiki = plant.getString("wiki");
                        String line = nameByUser;
                        allPlants.add(line);
                        adapter.add(line);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No plants", Toast.LENGTH_SHORT).show();
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
}
