package com.example.zyra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.LocalDatabase.DatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    protected Button buttonLogOut;

    protected DatabaseHelper databaseHelper;
    protected String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DatabaseHelper db = new DatabaseHelper(this);

        setupUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        // get user id from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PlantName", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        System.out.println("kasra" + userID);

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer deleteRow = databaseHelper.deleteUser(userID);

                if(deleteRow > 0)
                    Toast.makeText(SettingsActivity.this, "SIGNED OUT", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SettingsActivity.this, "ERROR IN SIGN OUT", Toast.LENGTH_SHORT).show();

                goToLoginSignOut();
            }
        });

    }

    public void setupUI(){
        buttonLogOut = findViewById(R.id.btnSignOut);
    }

    public void goToLoginSignOut(){
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
