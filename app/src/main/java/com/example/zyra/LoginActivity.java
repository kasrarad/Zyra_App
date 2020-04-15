package com.example.zyra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zyra.Database.Register;
import com.example.zyra.LocalDatabase.DatabaseHelper;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.PlantLocalDatabase.PlantDbHelper;

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
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    protected EditText editUsername;
    protected EditText editPw;
    protected TextView textRegister;
    protected Button btnLogin;

    protected String[] userData = new String[4];
    protected DatabaseHelper databaseHelper;
    protected PlantDbHelper plantDbHelper;

    private static final String TAG = "LoginActivity";

    int plantListSize;
    String id;
    String userID;
    String nameBySpecies;
    String nameByUser;
    String temperature;
    String moisture;
    String previousMoisturesLevel;
    String image;
    String wiki;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setTitle("Login");

        setupUI();

        databaseHelper = new DatabaseHelper(this);

        List<UserInfoDB> checkUserDB = databaseHelper.getAllUserInfo();

        //Check local database
        //If user's info does not exist, ask user to login
        if(checkUserDB.size() != 0){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else{

            String text = "Don't have an account? Register";
            SpannableString ss = new SpannableString(text);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    btnSignUp();
                }
            };

            ss.setSpan(clickableSpan, 23,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            textRegister.setText(ss);
            textRegister.setMovementMethod(LinkMovementMethod.getInstance());

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Login");
                    String usernameValue = editUsername.getText().toString();
                    String passwordValue = editPw.getText().toString();

                        if(usernameValue.isEmpty() || passwordValue.isEmpty() ) {
                            Toast.makeText(LoginActivity.this, "Please enter your information", Toast.LENGTH_SHORT).show();
                        } else {
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
        textRegister = findViewById(R.id.textViewRegister);
        btnLogin = findViewById(R.id.btnLogin);
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

                        GetPlantInfo getPlantInfo = new GetPlantInfo();
                        getPlantInfo.execute(userData[0]);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Your login credentials don't match an account in our system. Please try again. ", Toast.LENGTH_SHORT).show();
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


    //Get Plants Info
    class GetPlantInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            // Define URL
            String plant_url;

            String result = "";

            // Define URL
            plant_url = "http://zyraproject.ca/selectplant.php";

            try {

                // Extract the values
                String userID = params[0];

                URL url = new URL(plant_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                // Create data URL that we want to post
                String post_data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                // Write post data to the BufferWriter
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                // Read the response from post request
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
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

            if (result != null) {
                //Parsing jason Data
                try {
                    plantDbHelper = new PlantDbHelper(LoginActivity.this);
                    plantDbHelper.deleteAll();
                    JSONObject jasonResult = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

                    int success = Integer.parseInt(jasonResult.getString("success"));
                    if (success == 1) {
                        JSONArray plants = jasonResult.getJSONArray("plants");
                        plantListSize = plants.length();
                        for (int i = 0; i < plants.length(); i++) {
                            JSONObject plant = plants.getJSONObject(i);
                            int id = plant.getInt("id");
                            userID = plant.getString("userID");
                            nameBySpecies = plant.getString("nameBySpecies");
                            nameByUser = plant.getString("nameByUser");
                            temperature = plant.getString("temperature");
                            moisture = plant.getString("moisture");
                            previousMoisturesLevel = plant.getString("previousMoisturesLevel");
                            image = plant.getString("image");
                            wiki = plant.getString("wiki");
                            plantDbHelper.saveToLocalDatabase(new PlantInfoDB(userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki, PlantConfig.SYNC_STATUS_OK));
                        }
                        plantDbHelper.close();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error ", e.getMessage());
                }

            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

}
