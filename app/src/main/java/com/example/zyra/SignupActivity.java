package com.example.zyra;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    protected EditText editName;
    protected EditText editUsername;
    protected EditText editPw;
    protected EditText editConfirmPw;
    protected Button btnRegister;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Sign Up");

        setupUI();
    }

    public void setupUI(){
        editName = findViewById(R.id.editTextName);
        editUsername = findViewById(R.id.editTextUsername);
        editPw = findViewById(R.id.editTextPassword);
        editConfirmPw = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
    }
}
