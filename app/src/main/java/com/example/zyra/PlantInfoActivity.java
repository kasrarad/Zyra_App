package com.example.zyra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PlantInfoActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series1;
    protected Button btnImage;
    protected ImageView imagePlant;
    protected TextView textMyPlant;

    protected double x,y;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantinfo);

        setupUI();
        setGraph();


        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //system os is less than marshmellow
                    pickImageFromGallery();
                }
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

    //handle result of picked image


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            imagePlant.setImageURI(data.getData());
        }
    }

    public void setupUI() {
        btnImage = findViewById(R.id.buttonImage);
        imagePlant = findViewById(R.id.imagePlant);
        textMyPlant = findViewById(R.id.textViewMyPlant);
    }

    public void setGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("Moisture Level");
        graph.getGridLabelRenderer();
        graph.getViewport().setMaxX(24);
        graph.getViewport().setMaxY(100);

        x = 0;
        series1 = new LineGraphSeries<>();

        int numDataPoint = 1000;
        for(int i = 0; i < numDataPoint; i++){
            x = x + 0.1;
            y = x + 2;
            series1.appendData(new DataPoint(x,y),true,100);
        }
        graph.addSeries(series1);
    }

}
