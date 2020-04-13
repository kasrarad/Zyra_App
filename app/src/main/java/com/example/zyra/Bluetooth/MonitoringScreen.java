package com.example.zyra.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Date;

import java.util.UUID;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;

import com.example.zyra.Database.AddPlants;
import com.example.zyra.Database.UpdateMoisture;
import com.example.zyra.MainActivity;
import com.example.zyra.PlantActivity;
import com.example.zyra.PlantInfoActivity;
import com.example.zyra.PlantInfoDB;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.PlantLocalDatabase.PlantDbHelper;
import com.example.zyra.PlantsListView.PlantListViewAdapter;
import com.example.zyra.R;

public class MonitoringScreen extends Activity {

    protected Thread workerThread = new Thread();

    protected String data = "";
    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private InputStream inStream = null;
    boolean stopWorker = false;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];
    Handler handler = new Handler();

    private String reading = "";

    private boolean mIsUserInitiatedDisconnect = false;

    // All controls here
    private TextView mTxtReceive;
//    private Button mBtnClearInput;
    //    private ScrollView scrollView;
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;
    private Button mBtnBack;
    private TextView mTxtReading;


    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    protected int time = 0;


    protected PlantDbHelper plantDbHelper;
    protected String plantName;
    protected String plantID;
    protected String userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki;
    protected Integer id, syncstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        ActivityHelper.initialize(this);

        setupUI();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(BluetoothActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(BluetoothActivity.DEVICE_UUID));
        mMaxChars = b.getInt(BluetoothActivity.BUFFER_SIZE);
        // get plant's name and ID
        plantName = intent.getStringExtra("nameByUser");
        plantID = intent.getStringExtra("plantID");
        Log.d(TAG, "Ready");

//        mTxtReceive.setMovementMethod(new ScrollingMovementMethod());

//        mBtnClearInput.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                mTxtReceive.setText("");
//            }
//        });

        mBtnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorker = true;
                goToPlantActivity();
            }
        });

    }

    public void setupUI() {
//        mTxtReceive = findViewById(R.id.txtReceive);
//        chkScroll = findViewById(R.id.chkScroll);
//        chkReceiveText = findViewById(R.id.chkReceiveText);
//        scrollView = findViewById(R.id.viewScroll);
//        mBtnClearInput = findViewById(R.id.btnClearInput);
        mBtnBack = findViewById(R.id.btnBack);
        mTxtReading = findViewById(R.id.textViewRead);
    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }


        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);
                        reading = strInput;

                        listenForData();


                    }
                    Thread.sleep(1500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MonitoringScreen.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                // Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }

    protected void goToPlantActivity() {
        Intent intent = new Intent(MonitoringScreen.this, PlantActivity.class);
        startActivity(intent);
    }

    public void listenForData() {

        try {
            inStream = mBTSocket.getInputStream();
        } catch (IOException e) {
        }

         workerThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = inStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == 10) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    data = new String(encodedBytes, "US-ASCII");
                                    time = LocalTime.now().getHour();
                                    time = time * 2;
                                    readBufferPosition = 0;
                                    data = "54";
                                    handler.post(new Runnable() {
                                        public void run() {

                                            String moistureNumberOnly= data.replaceAll("[^0-9]", "");

//                                            System.out.println("data: " + data);

                                            if(Integer.parseInt(moistureNumberOnly) >= 100){
                                                String data2  = "Moisture: 100%";
                                                mTxtReading.setText(data2);
                                                moistureNumberOnly = "100";
//                                                System.out.println("data2: " + data);
                                            }
                                            else{
                                                mTxtReading.setText(data);
                                            }

                                            //**********************************
                                            //**SEND THIS PART TO THE DATABASE**
                                            //**********************************

                                            //Send moistureNumberOnly to the database at current moisture level
                                            //THIS STRING -> moistureNumberOnly;

                                            String currentMoisture = moistureNumberOnly;


                                            //This is for previous moisture levels
                                            String previousMoisture = moistureNumberOnly;
                                            if(Integer.parseInt(moistureNumberOnly) > 99){
                                                previousMoisture = "99"; //get it to 2 chars in length
                                            }

                                            if( Integer.parseInt(moistureNumberOnly) < 10){
                                                //get it to 2 chars in length
                                                previousMoisture = "0";
                                                previousMoisture += moistureNumberOnly;
                                            }

                                            if (Integer.parseInt(previousMoisture) < 1){
                                                previousMoisture = "01";
                                            }

                                            //***************************************************************
                                            //**UNCOMMENT BELOW, ITS IMPORTANT FOR KASRA TO SEND INFO TO DB**
                                            //***************************************************************

                                            // Here it needs to pull the previousMoistureLevel string already in the database to the string previousMoistureLevel
                                            //  String previousMoistureLevel = previous moisture level String from database
                                            readFromLocalDatabase();
                                            String previousMoistureLevel = previousMoisturesLevel;

                                            //this is just to prevent bugs/errors
                                            if ( previousMoistureLevel.charAt(0) == '[' || (previousMoistureLevel.length() < 48) ) {

                                                previousMoistureLevel = "";
                                                for(int i = 0; i < 48 ;i ++){
                                                    previousMoistureLevel +="0";
                                                }

                                            }

                                            //replace part of the string based on current time
                                            StringBuilder replacePrev = new StringBuilder(previousMoistureLevel);
                                            replacePrev.setCharAt(time, previousMoisture.charAt(0));
                                            replacePrev.setCharAt((time + 1),previousMoisture.charAt(1));
                                            System.out.println("replacePrev" + replacePrev);
                                            System.out.println("prevMoisturelvl " + previousMoistureLevel);
                                            System.out.println("prevMoisture " + previousMoisture);

                                            //Now send replacePrev string to the database in the part of the previous moisture readings
                                            //Database Previous moisture readings  = replacePrev
                                            String previousMoistures = replacePrev.toString();
                                            savePlant(currentMoisture, previousMoistures);
                                            System.out.println(previousMoistures);

                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();

    }

    private void readFromLocalDatabase(){

        plantDbHelper = new PlantDbHelper(this);

        List<PlantInfoDB> plants = plantDbHelper.readFromLocalDatabase();

        for (int i=0; i<plants.size(); i++){
            if(plants.get(i).getNameByUser().equals(plantName)){
                // Retrieve the row in each iteration
                id = plants.get(i).getID();
                userID = plants.get(i).getUserID();
                nameBySpecies = plants.get(i).getNameBySpecies();
                nameByUser = plants.get(i).getNameByUser();
                temperature = plants.get(i).getTemperature();
                moisture = plants.get(i).getMoisture();
                previousMoisturesLevel = plants.get(i).getPreviousMoisturesLevel();
                image = plants.get(i).getImage();
                wiki = plants.get(i).getWiki();
                syncstatus = plants.get(i).getSyncstatus();
            }
        }

        plantDbHelper.close();

    }

    // Add plants to the database
    public void savePlant(String currentMoisture, String previousMoistures) {
        if(checkNetworkConnection()){
            UpdateMoisture updateMoisture = new UpdateMoisture(this);
            updateMoisture.execute(userID, nameBySpecies, nameByUser, temperature, currentMoisture, previousMoistures, image, wiki);
            editLocalStorage(new PlantInfoDB(id,userID, nameBySpecies, nameByUser, temperature, currentMoisture, previousMoistures, image, wiki, PlantConfig.SYNC_STATUS_OK));
        } else{
            editLocalStorage(new PlantInfoDB(id, userID, nameBySpecies, nameByUser, temperature, currentMoisture, previousMoistures, image, wiki, PlantConfig.SYNC_STATUS_FAILED));
        }
    }

    private void editLocalStorage(PlantInfoDB plantInfoDB){
        PlantDbHelper plantDbHelper = new PlantDbHelper(this);
        plantDbHelper.updateLocalDatabase(plantInfoDB);
        plantDbHelper.close();
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }

}