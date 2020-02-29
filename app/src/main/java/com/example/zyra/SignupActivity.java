package com.example.zyra;

import android.content.Context;
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

                preferences = getSharedPreferences(getString(R.string.profilefile), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();


                if(nameValue.isEmpty() || usernameValue.isEmpty() || passwordValue.isEmpty() || confirmpassValue.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                }else if(!passwordValue.equals(confirmpassValue)) {
                    Toast.makeText(SignupActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignupActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                    editor.putString(getString(R.string.profilename), nameValue);
                    editor.putString(getString(R.string.profileusername), usernameValue);
                    editor.putString(getString(R.string.profilepassword), passwordValue);
                    editor.apply();
                    goToLogin();
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
    }

    protected void goToLogin(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
