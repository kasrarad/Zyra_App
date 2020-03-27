package com.example.zyra;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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
import java.io.File;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class PlantInfoActivity extends AppCompatActivity {



    private LineGraphSeries<DataPoint> series1;
    protected ImageButton btnImage;
    protected TextView textMyPlantName;
    protected TextView textMyPlantType;
    protected CircleImageView circleImgPlant;
    protected Button btnConfirm;
    String plantName;
    String plantSpecies;
    String plantPreviousMoisture;

    private ProgressDialog progressDialog;


    protected double x,y;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantinfo);
        setupUI();


        // get plant's name
        plantName = getIntent().getStringExtra("nameByUser");
        System.out.println("nameByUser: " + plantName);
        textMyPlantName.setText(plantName);



        plantSpecies = getIntent().getStringExtra("nameBySpecies");
        textMyPlantType.setText(plantSpecies);

        System.out.println("Plant Species:" + plantSpecies);

        plantPreviousMoisture = getIntent().getStringExtra("previousMoisture");
//        System.out.println("Plant Moisture: " + plantPreviousMoisture);


        setGraph();


        btnImage.setOnClickListener(new View.OnClickListener() {
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
        Dexter.withActivity(PlantInfoActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(PlantInfoActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlantInfoActivity.this);
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
                btnConfirm.setVisibility(View.VISIBLE);

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File imageFile = new File(resultUri.getPath());
                        progressDialog.show();

                        //would make it "fold" and it will not only be invisible but also won't take up space in the layuout either
                        btnConfirm.setVisibility(View.GONE);

                        AndroidNetworking.upload("http://zyraproject.ca/insertimage.php")
                                .addMultipartFile("image", imageFile)
                                .addMultipartParameter("userId", String.valueOf(11))
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        float progress = (float) bytesUploaded/totalBytes * 100;
                                        progressDialog.setProgress((int) progress);
                                    }
                                })
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            progressDialog.dismiss();
                                            JSONObject jsonObject = new JSONObject(response);
                                            int status = jsonObject.getInt("status");
                                            String message = jsonObject.getString("message");
                                            if(status == 0) {
                                                Toast.makeText(PlantInfoActivity.this, "Unable to upload image" + message,
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PlantInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        anError.printStackTrace();
                                        Toast.makeText(PlantInfoActivity.this,
                                                "Error Uploading Image", Toast.LENGTH_SHORT);
                                    }
                                });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void setupUI() {
        textMyPlantName = findViewById(R.id.textViewPlantName);
        textMyPlantType = findViewById(R.id.textViewPlantType);
        btnConfirm = findViewById(R.id.buttonConfirm);
        btnImage = findViewById(R.id.buttonImage);
        circleImgPlant = findViewById(R.id.imagePlant);

        progressDialog = new ProgressDialog(PlantInfoActivity.this);
        progressDialog.setMessage("Uploading Image . . .");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public void setGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("Moisture Level");
        graph.getGridLabelRenderer();
        graph.getViewport().setMaxX(24);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setMinY(0);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        x = 0;
        series1 = new LineGraphSeries<>();

        int numDataPoint = 24;
        for(int i = 0; i < numDataPoint; i = i + 2){


            String value ="00";
            StringBuilder number = new StringBuilder("00");

//            number.setCharAt(0, plantPreviousMoisture.charAt(i));
//            number.setCharAt(1,plantPreviousMoisture.charAt(i + 1));

            value = number.toString();
            System.out.println("Y: " + number +" Double check: " + value);

            x = i;
            y = Integer.parseInt(value);
            series1.appendData(new DataPoint(x,y),true,100);
        }
        graph.addSeries(series1);
    }

}
