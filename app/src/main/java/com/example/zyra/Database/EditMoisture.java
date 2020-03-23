package com.example.zyra.Database;
import android.content.Context;

import android.os.AsyncTask;

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

public class EditMoisture extends AsyncTask<String, Void, String> {

    Context context;
    String id;

    public EditMoisture(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        // Define URL
        String plant_url;

        String result = "";

        // Extract the id of the plant
        id = params[0];

        // Define URL
        plant_url = "http://zyraproject.ca/updatemoisture.php";
        try {
            // Extract the values (type of operation)
            String userID = params[1];
            String nameBySpecies = params[2];
            String nameByUser = params[3];
            String temperature = params[4];
            String moisture = params[5];
            String previousMoisturesLevel = params[6];
            String image = params[7];
            String wiki = params[8];

            URL url = new URL(plant_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            // Create data URL that we want to post
            String post_data = URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8")+"&"
                    +URLEncoder.encode("userID", "UTF-8")+"="+URLEncoder.encode(userID, "UTF-8")+"&"
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

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
