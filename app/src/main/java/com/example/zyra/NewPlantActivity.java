package com.example.zyra;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.AddPlants;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.PlantLocalDatabase.PlantDbHelper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewPlantActivity extends AppCompatActivity {

    protected EditText editNewPlantName;
    protected EditText editNewPlantType;
    protected Button btnCancelPlant;

    protected ImageButton btnImage;
    protected CircleImageView circleImgPlant;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    protected MoistureData moistureData = new MoistureData();


    // Add Plants
    //EditText nameEditText, nameByUserEditText, temperatureEditText, moistureEditText, imageEditText, wikiEditText;
    String userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki;

    String imageUri = "";

    private static final String TAG = "NewPlantActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplant);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Add New Plant");

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        setupUI();

        btnCancelPlant.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goBackPlantList();
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    public void setupUI(){

        editNewPlantName = findViewById(R.id.editTextNewName);
        editNewPlantType = findViewById(R.id.editTextNewType);
        btnCancelPlant = findViewById(R.id.btnCancelPlant);

        btnImage = findViewById(R.id.buttonImage);
        circleImgPlant = findViewById(R.id.imagePlant);

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
        Dexter.withActivity(NewPlantActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(NewPlantActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(NewPlantActivity.this);
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
                circleImgPlant.setImageURI(resultUri);

                imageUri = resultUri.toString();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void goBackPlantList() {
        Intent intent = new Intent(NewPlantActivity.this, PlantActivity.class);
        startActivity(intent);
    }

    // Save Plants Button
    // Add plants to the database
    public void savePlantsButton(View view) {
        //nameBySpecies = nameEditText.getText().toString();
        nameBySpecies = editNewPlantType.getText().toString().trim();
        nameByUser = editNewPlantName.getText().toString().trim();
        //temperature = temperatureEditText.getText().toString();
        temperature = "";

        // Add dummy values to Moisture data
        //moistureData.addMoistureData();
        moisture = moistureData.getCurrentMoistureLevel();
        previousMoisturesLevel = moistureData.getPreviousMoistureLevels();

        image = imageUri;
        wiki = "";

        String type = "Add";

        if (!nameByUser.trim().isEmpty()) {

            if(checkNetworkConnection()){
                AddPlants addPlants = new AddPlants(this);
                addPlants.execute(type, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);
                saveToLocalStorage(new PlantInfoDB(userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki, PlantConfig.SYNC_STATUS_OK));
            } else{
                saveToLocalStorage(new PlantInfoDB(userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki, PlantConfig.SYNC_STATUS_FAILED));
            }

            Intent intent = new Intent(this, PlantActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToLocalStorage(PlantInfoDB plantInfoDB){
        PlantDbHelper plantDbHelper = new PlantDbHelper(this);
        plantDbHelper.saveToLocalDatabase(plantInfoDB);
        plantDbHelper.close();
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }
}



