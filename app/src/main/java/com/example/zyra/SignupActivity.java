package com.example.zyra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    protected EditText editName;
    protected EditText editUsername;
    protected EditText editPw;
    protected EditText editConfirmPw;
    protected Button btnRegister;
    protected Button btnCancel;

    SharedPreferences preferences;


    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Sign Up");

        setupUI();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Register");

                String usernameValue = editUsername.getText().toString();
                String nameValue = editName.getText().toString();
                String passwordValue = editPw.getText().toString();
                String confirmpassValue = editConfirmPw.getText().toString();

                    //Upload data to database.

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }


    public void setupUI(){
        editName = findViewById(R.id.editTextName);
        editUsername = findViewById(R.id.editTextUsername);
        editPw = findViewById(R.id.editTextPassword);
        editConfirmPw = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
    }

    public void goToLogin(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
