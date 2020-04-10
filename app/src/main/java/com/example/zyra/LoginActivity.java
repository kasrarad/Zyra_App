package com.example.zyra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.LocalDatabase.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    protected EditText editUsername;
    protected EditText editPw;
    protected Button btnRegister;
    protected Button btnLogin;
    private CheckBox checkBoxRememberMe;

    protected String[] userData = new String[4];
    protected DatabaseHelper databaseHelper;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "PrefsFile";

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Login");

        setupUI();

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        getPreferencesData();

        databaseHelper = new DatabaseHelper(this);

        List<UserInfoDB> checkUserDB = databaseHelper.getAllUserInfo();

        //Check local database
        //If user's info does not exist, ask user to login
        if(checkUserDB.size() != 0){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else{
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

                    if (!checkBoxRememberMe.isChecked()) {

                        if(usernameValue.isEmpty() || passwordValue.isEmpty() ) {
                            Toast.makeText(LoginActivity.this, "Please enter your information", Toast.LENGTH_SHORT).show();
                        } else {
                            Login login = new Login(LoginActivity.this);
                            login.execute(usernameValue, passwordValue);
                        }

                    } else {
                        if(usernameValue.isEmpty() || passwordValue.isEmpty() ) {
                            Toast.makeText(LoginActivity.this, "Please enter your information", Toast.LENGTH_SHORT).show();
                        } else {
                            Boolean boolIsChecked = checkBoxRememberMe.isChecked();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("pref_name", editUsername.getText().toString());
                            editor.putBoolean("pref_check", boolIsChecked);
                            editor.apply();
                            Toast.makeText(LoginActivity.this, "Checked!", Toast.LENGTH_SHORT).show();
                            Login login = new Login(LoginActivity.this);
                            login.execute(usernameValue, passwordValue);
                        }
                    }

//                    if(usernameValue.isEmpty() || passwordValue.isEmpty())
//                        Toast.makeText(LoginActivity.this, "Please enter your information", Toast.LENGTH_SHORT).show();
//                    else{
//                        Login login = new Login(LoginActivity.this);
//                        login.execute(usernameValue, passwordValue);
//                    }
                }
            });
        }

        databaseHelper.close();
    }

    public void btnSignUp(){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void setupUI(){
        editUsername = findViewById(R.id.editTextUsername);
        editPw = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        checkBoxRememberMe = findViewById(R.id.checkBoxRemember);
    }

    //Login and user's info
    class Login extends AsyncTask<String, Void, String> {

        Context context;

        public Login(Context context){
            this.context = context;
        }


        @Override
        protected String doInBackground(String... params) {

            String login_url = "http://zyraproject.ca/login.php";

            try {
                String user_name = params[0];
                String password = params[1];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                //Parsing jason Data
                try {
                    JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

                    int success = Integer.parseInt(jasonResult.getString("success"));
                    if (success == 1) {
                        JSONArray usersInfo = jasonResult.getJSONArray("users");
                        for (int i = 0; i < usersInfo.length(); i++) {
                            JSONObject userInfo = usersInfo.getJSONObject(i);
                            int id = userInfo.getInt("id");
                            String name = userInfo.getString("name");
                            String username = userInfo.getString("username");
                            String password = userInfo.getString("password");
                            userData[0] = String.valueOf(id);
                            userData[1] = name;
                            userData[2] = username;
                            userData[3] = password;
                        }

                        // Save User Information in local Database
                        databaseHelper.insertUserInfo(new UserInfoDB(userData[0], userData[1],userData[2],userData[3]));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Some of your information isn't correct. Please try again", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error ", e.getMessage());
                }
            } else{
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Some of your information isn't correct. Please try again", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void getPreferencesData() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sp.contains("pref_name")) {
            String u = sp.getString("pref_name", "not found.");
            editUsername.setText(u);
        }
        if (sp.contains("pref_name")) {
            Boolean bool = sp.getBoolean("pref_check", false);
            checkBoxRememberMe.setChecked(bool);
        }
    }

}
