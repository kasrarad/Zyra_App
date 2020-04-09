package com.example.zyra;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.DeletePlants;
import com.example.zyra.Database.EditPlants;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPlantActivity extends AppCompatActivity {

    protected String[] plantInfo = new String[9];

    protected ImageButton addButtonImage;
    protected CircleImageView imagePlant;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    // Edit Plants
    private EditText editOldPlantName;
    private EditText editOldPlantType;
    //EditText nameEditText, nameByUserEditText, temperatureEditText, moistureEditText, imageEditText, wikiEditText;
    String id, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, wiki;

    String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        // Add back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addButtonImage = findViewById(R.id.addButtonImage);
        imagePlant = findViewById(R.id.imagePlant);

        // Edit Plants
        editOldPlantName = (EditText) findViewById(R.id.editTextOldName);
        editOldPlantType = findViewById(R.id.editTextOldType);

        // get plant's name
        String badPlantName = getIntent().getStringExtra("nameByUser");
        String[] plantNameSplit = badPlantName.split("\n");
        String plantName = plantNameSplit[0];
        System.out.println(plantName);

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);
        System.out.println("USer ID : " + userID);

        //Connect to the database and get data
        new GetPlantInfo().execute(userID, plantName);

        addButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

    }

    private void pickImageFromGallery() {
        //intent to pick plant image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void checkPermission() {
        Dexter.withActivity(EditPlantActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(EditPlantActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditPlantActivity.this);
                            builder.setTitle("Permission Required")
                                    .setMessage("Permission to access gallery is required to choose a plant image." +
                                            "Please go to settings to enable storage permission. ")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, 100);
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    //handle result of picked image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();
                imagePlant.setImageURI(resultUri);

                plantInfo[7] = resultUri.toString();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
                        String previousMoisturesLevel = plant.getString("previousMoisturesLevel");
                        String image1 = plant.getString("image");
                        String wiki = plant.getString("wiki");
                        plantInfo[0] = String.valueOf(id);
                        plantInfo[1] = userID;
                        plantInfo[2] = nameBySpecies;
                        editOldPlantType.setText(plantInfo[2]);
                        plantInfo[3] = nameByUser;
                        editOldPlantName.setText(plantInfo[3]);
                        plantInfo[4] = temperature;
                        plantInfo[5] = moisture;
                        plantInfo[6] = previousMoisturesLevel;
                        plantInfo[7] = image1;
                        image = image1;
                        plantInfo[8] = wiki;

                        if(!image.equals("")){
                            Uri uri = Uri.parse(image);
                            imagePlant.setImageURI(uri);
                        }

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
        nameBySpecies = editOldPlantType.getText().toString();
        //nameByUser = plantInfo[3];
        nameByUser = editOldPlantName.getText().toString();
        temperature = plantInfo[4];
        moisture = plantInfo[5];
        previousMoisturesLevel = plantInfo[6];
        image = plantInfo[7];
        wiki = plantInfo[8];

        if(!nameByUser.trim().isEmpty()) {

            EditPlants editPlants = new EditPlants(this);
            editPlants.execute(id, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);
        }else{
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
        }
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