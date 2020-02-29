package com.example.zyra.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.zyra.MainActivity;

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

public class DeletePlants extends AsyncTask<String, Void, String> {


    Context context;
    String id;

    public DeletePlants(Context context){
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
    plant_url = "http://zyraproject.ca/deleteplant.php";

    try {

        URL url = new URL(plant_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

        // Create data URL that we want to post
        String post_data = URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");
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

        Toast.makeText(context, "Plant Deleted", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
