package com.example.zyra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.Register;

public class SignupActivity extends AppCompatActivity {

    protected EditText editName;
    protected EditText editUsername;
    protected EditText editPw;
    protected EditText editConfirmPw;
    protected Button btnRegister;
    protected Button btnCancel;
    protected TextView textSignUp;

    private static final String TAG = "SignupActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: Started");

        setupUI();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Register");

                String usernameValue = editUsername.getText().toString();
                String nameValue = editName.getText().toString();
                String passwordValue = editPw.getText().toString();
                String confirmpassValue = editConfirmPw.getText().toString();

                if(nameValue.isEmpty() || usernameValue.isEmpty() || passwordValue.isEmpty() || confirmpassValue.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                }else if(!passwordValue.equals(confirmpassValue)) {
                    Toast.makeText(SignupActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                }else{
                    Register register = new Register(SignupActivity.this);
                    register.execute(nameValue, usernameValue, passwordValue);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }


    protected void setupUI(){
        editName = findViewById(R.id.editTextName);
        editUsername = findViewById(R.id.editTextUsername);
        editPw = findViewById(R.id.editTextPassword);
        editConfirmPw = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        textSignUp = findViewById(R.id.textViewSignUp);
    }

    protected void goToLogin(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    protected void goToSignup(){
        Intent intent = new Intent(SignupActivity.this, SignupActivity.class);
        startActivity(intent);
    }

}
