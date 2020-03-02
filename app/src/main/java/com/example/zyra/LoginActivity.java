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

public class LoginActivity extends AppCompatActivity {

    protected EditText editUsername;
    protected EditText editPw;
    protected Button btnRegister;
    protected Button btnLogin;

    protected SharedPreferences preferences;

    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Login");

        setupUI();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Register");
                btnSignUp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Login");
                String usernameValue = editUsername.getText().toString();
                String passwordValue = editPw.getText().toString();

                String registeredUsername = preferences.getString("profileusername", "");
                String registeredPassword = preferences.getString("profilepassword", "");

                if (usernameValue.equals(registeredUsername) && passwordValue.equals(registeredPassword))
                btnLogin();
                else if(usernameValue.isEmpty() || passwordValue.isEmpty())
                    Toast.makeText(LoginActivity.this, "Please enter your information", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LoginActivity.this, "Some of your information isn't correct. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnSignUp(){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void btnLogin(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void setupUI(){
        editUsername = findViewById(R.id.editTextUsername);
        editPw = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        preferences = getSharedPreferences("UserInfo",0);
    }

}
