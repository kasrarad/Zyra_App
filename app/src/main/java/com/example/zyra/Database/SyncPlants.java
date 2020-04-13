package com.example.zyra.Database;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.zyra.PlantActivity;
import com.example.zyra.PlantInfoDB;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.PlantLocalDatabase.PlantDbHelper;

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

public class SyncPlants extends AsyncTask<String, Void, String> {
    Context context;
    String userID;
    String nameBySpecies;
    String nameByUser;
    String temperature;
    String moisture;
    String previousMoisturesLevel;
    String image;
    String wiki;

    public SyncPlants(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        // Define URL
        String plant_url;

        String result = "";

        // Define URL
        plant_url = "http://zyraproject.ca/editplant.php";
        try {
            // Extract the values (type of operation)
            userID = params[0];
            nameBySpecies = params[1];
            nameByUser = params[2];
            temperature = params[3];
            moisture = params[4];
            previousMoisturesLevel = params[5];
            image = params[6];
            wiki = params[7];

            URL url = new URL(plant_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            // Create data URL that we want to post
            String post_data = URLEncoder.encode("userID", "UTF-8")+"="+URLEncoder.encode(userID, "UTF-8")+"&"
                    +URLEncoder.encode("nameBySpecies", "UTF-8")+"="+URLEncoder.encode(nameBySpecies, "UTF-8")+"&"
                    +URLEncoder.encode("nameByUser", "UTF-8")+"="+URLEncoder.encode(nameByUser, "UTF-8")+"&"
                    +URLEncoder.encode("temperature", "UTF-8")+"="+URLEncoder.encode(temperature, "UTF-8")+"&"
                    +URLEncoder.encode("moisture", "UTF-8")+"="+URLEncoder.encode(moisture, "UTF-8")+"&"
                    +URLEncoder.encode("previousMoisturesLevel", "UTF-8")+"="+URLEncoder.encode(previousMoisturesLevel, "UTF-8")+"&"
                    +URLEncoder.encode("image", "UTF-8")+"="+URLEncoder.encode(image, "UTF-8")+"&"
                    +URLEncoder.encode("wiki", "UTF-8")+"="+URLEncoder.encode(wiki, "UTF-8")+"&";
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
/*
        PlantDbHelper plantDbHelper = new PlantDbHelper(context);
        PlantInfoDB plantInfoDB = new PlantInfoDB(userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki, PlantConfig.SYNC_STATUS_OK);
        plantDbHelper.updateLocalDatabase(plantInfoDB);
        plantDbHelper.close();
*/
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
