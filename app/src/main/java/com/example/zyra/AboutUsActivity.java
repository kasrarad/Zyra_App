package com.example.zyra;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    protected ImageView imageGarden;
    protected TextView textAboutText;
    protected TextView textAboutTitle;
    protected TextView textContactText;
    protected TextView textContactTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        setupUI();

        getSupportActionBar().setTitle("About Us");
    }

    public void setupUI() {
        imageGarden = findViewById(R.id.imageViewGarden);
        textAboutText = findViewById(R.id.textViewAboutText);
        textAboutTitle = findViewById(R.id.textViewAboutTitle);
        textContactText = findViewById(R.id.textViewContactText);
        textContactTitle = findViewById(R.id.textViewAboutTitle);
    }
}
