package com.example.zyra.Database;

import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.zyra.LoginActivity;
import com.example.zyra.NewPlantActivity;
import com.example.zyra.PlantActivity;
import com.example.zyra.SignupActivity;

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

public class AddPlants extends AsyncTask<String, Void, String>{

    Context context;
    String type;

    public AddPlants(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        // Define URL
        String plant_url;

        String result = "";

        // Extract the values (type of operation)
        type = params[0];

        if(type.equals("Add")){

            // Define URL
            plant_url = "http://zyraproject.ca/insertplant.php";
            try {
                // Extract the values (type of operation)
                String userID = params[1];
                String nameBySpecies = params[2];
                String nameByUser = params[3];
                String temperature = params[4];
                String moisture = params[5];
                String image = params[6];
                String wiki = params[7];

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
        }

        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {


        try {
            JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

            int success = Integer.parseInt(jasonResult.getString("success"));
            if (success == 1) {
                Intent intent = new Intent (context, PlantActivity.class);
                context.startActivity(intent);
                Toast.makeText(context, "Plant Added", Toast.LENGTH_SHORT).show();
            } else{
                Intent intent = new Intent (context, NewPlantActivity.class);
                context.startActivity(intent);
                Toast.makeText(context, "PLANT'S NAME HAS ALREADY EXISTS", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
