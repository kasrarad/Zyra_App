package com.example.zyra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.example.zyra.Bluetooth.MonitoringScreen;
import com.example.zyra.Bluetooth.PreferencesActivity;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlantInfoActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series2;
    private PointsGraphSeries<DataPoint> series1;
    protected SimpleDateFormat sdf = new SimpleDateFormat("kk");
    protected SimpleDateFormat sdf2 = new SimpleDateFormat("E");
    protected ImageButton btnImage;
    protected TextView textMyPlantName;
    protected TextView textMyPlantType;
    protected TextView graphXLabel;
    protected TextView textMyPlantMoisture;
    protected CircleImageView circleImgPlant;
    protected Button btnConfirm;
    String plantName;
    String plantSpecies;
    String plantPreviousMoisture;
    String plantCurrentMoisture;
    String plantImage;

    private ProgressDialog progressDialog;

    protected double x,y;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantinfo);
        setupUI();
        getSupportActionBar().setTitle("My Plant List");

        // get plant's name
        plantName = getIntent().getStringExtra("nameByUser");
        System.out.println("nameByUser: " + plantName);
        textMyPlantName.setText(plantName);

        plantSpecies = getIntent().getStringExtra("nameBySpecies");
        textMyPlantType.setText(plantSpecies);
        System.out.println("Plant Species:" + plantSpecies);

        plantPreviousMoisture = getIntent().getStringExtra("previousMoisture");
        System.out.println("Plant Moisture: " + plantPreviousMoisture);

        if ( plantPreviousMoisture.charAt(0) == '[' || (plantPreviousMoisture.length() < 47) ) {

            plantPreviousMoisture = "";
            for(int i = 0; i < 48 ;i ++){
                plantPreviousMoisture +="0";
            }

        }
        System.out.println("Plant Moisture: " + plantPreviousMoisture);

        plantCurrentMoisture = getIntent().getStringExtra("currentMoisture");
        textMyPlantMoisture.setText("Last Read: " + plantCurrentMoisture);


        //test numbers
//        plantPreviousMoisture = "998070605040302010009985756545352515059980503000";


        plantImage = getIntent().getStringExtra("image");
        if(!plantImage.equals("")){
            Uri uri = Uri.parse(plantImage);
            circleImgPlant.setImageURI(uri);
        }

        setGraph();
    }

    public void setupUI() {
        textMyPlantName = findViewById(R.id.textViewPlantName);
        textMyPlantType = findViewById(R.id.textViewPlantType);
        graphXLabel = findViewById(R.id.graphXLabel);
        textMyPlantMoisture = findViewById(R.id.textViewPlantMoisture);

        graphXLabel.setText("Number of hours ago");
        circleImgPlant = findViewById(R.id.plantImage);

        progressDialog = new ProgressDialog(PlantInfoActivity.this);
        progressDialog.setMessage("Uploading Image . . .");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//
//        listView = findViewById(R.id.listview);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setGraph() {

        GraphView graph = findViewById(R.id.graph);
        graph.setTitle("Past 24 Hour Moisture Level");
        graph.getGridLabelRenderer();
        graph.getViewport().setMaxX(25);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setMinY(0);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("              24                  18                  12                   6               Now");
        graph.getGridLabelRenderer().setVerticalAxisTitle("% Moisture Level");
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);


        series1 = new PointsGraphSeries<>(getDataPoint());
        series1.setSize(10);
        series2 = new LineGraphSeries<>(getDataPoint());
        graph.addSeries(series1);
        graph.addSeries(series2);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected DataPoint[] getDataPoint() {

        int time = LocalTime.now().getHour();

        Integer[] prev = getPrevMoistArray();
//        Integer[] hour= getHourDisplay();

        DataPoint[] dp = new DataPoint[]{

                new DataPoint( 0, prev[1]),
                new DataPoint( 1, prev[2]),
                new DataPoint( 2, prev[3]),
                new DataPoint( 3, prev[4]),
                new DataPoint( 4, prev[5]),
                new DataPoint( 5, prev[6]),
                new DataPoint( 6, prev[7]),
                new DataPoint( 7, prev[8]),
                new DataPoint( 8, prev[9]),
                new DataPoint( 9, prev[10]),
                new DataPoint( 10 , prev[11]),
                new DataPoint( 11 , prev[12]),
                new DataPoint( 12 , prev[13]),
                new DataPoint( 13 , prev[14]),
                new DataPoint( 14 , prev[15]),
                new DataPoint( 15 , prev[16]),
                new DataPoint( 16 , prev[17]),
                new DataPoint( 17 , prev[18]),
                new DataPoint( 18 , prev[19]),
                new DataPoint( 19 , prev[20]),
                new DataPoint( 20 , prev[21]),
                new DataPoint( 21 , prev[22]),
                new DataPoint( 22 , prev[23]),
                new DataPoint( 23 , prev[0])

        };
        return dp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected Integer[] getPrevMoistArray(){

        int time = LocalTime.now().getHour();
        Integer[] prev = new Integer[24];
        System.out.println("time: " + time);
        int testing = time * 2;
        int newIndex = time;

        for (int i = 0; i < prev.length; i++) {

            newIndex++;
            System.out.println();
            if(newIndex > 24){
                newIndex = 0;
                testing = 0;
            }

            String value = "00";
            StringBuilder number = new StringBuilder("00");
            number.setCharAt(0, plantPreviousMoisture.charAt(testing));
            number.setCharAt(1, plantPreviousMoisture.charAt(testing + 1));
            testing = testing + 2;
            value = number.toString();
//            System.out.println("Time + i = " + (newIndex) +"  Value: " + value);

            prev[i] = Integer.parseInt(value);

        }

//        for (int i = 0 ; i < prev.length ; i++){
//            System.out.println(i + ": "+ prev[i]);
//        }
        return prev;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected Integer[] getHourDisplay(){
        Integer[] hour = new Integer[24];

        int time = LocalTime.now().getHour();

        int testing = 0;
        int numDataPoint = 24;
        for (int i = 0; i < numDataPoint; i++) {
            time++;
            hour[i] = time;
            if(time > 23){
                time = 0;
            }
        }

        for (int i = 0 ; i < hour.length ; i++){
            System.out.println(i + ": "+ hour[i]);
        }



        return hour;
    }

//    protected void onPause() {
//// TODO Auto-generated method stub
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//// TODO Auto-generated method stub
//        super.onStop();
//    }

//    /**
//     * Quick way to call the Toast
//     * @param str
//     */
//    private void msg(String str) {
//        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * Initialize the List adapter
//     * @param objects
//     */
//    private void initList(List<BluetoothDevice> objects) {
//        final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_item_reading, R.id.lstContent, objects);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                adapter.setSelectedIndex(position);
//                btnConnect.setEnabled(true);
//            }
//        });
//    }
//
//    /**
//     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
//     * will show up with this. I didn't see any need to re-build the wheel over here
//     * @author ryder
//     *
//     */
//    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {
//
//        @Override
//        protected List<BluetoothDevice> doInBackground(Void... params) {
//            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
//            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
//            for (BluetoothDevice device : pairedDevices) {
//                listDevices.add(device);
//            }
//            return listDevices;
//
//        }
//
//        @Override
//        protected void onPostExecute(List<BluetoothDevice> listDevices) {
//            super.onPostExecute(listDevices);
//            if (listDevices.size() > 0) {
//                MyAdapter adapter = (MyAdapter) listView.getAdapter();
//                adapter.replaceItems(listDevices);
//            } else {
//                msg("No paired devices found, please pair your serial BT device and try again");
//            }
//        }
//
//    }
//
//    /**
//     * Custom adapter to show the current devices in the list. This is a bit of an overkill for this
//     * project, but I figured it would be good learning
//     * Most of the code is lifted from somewhere but I can't find the link anymore
//     * @author ryder
//     *
//     */
//    private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
//        private int selectedIndex;
//        private Context context;
//        private int selectedColor = Color.parseColor("#abcdef");
//        private List<BluetoothDevice> myList;
//
//        public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
//            super(ctx, resource, textViewResourceId, objects);
//            context = ctx;
//            myList = objects;
//            selectedIndex = -1;
//        }
//
//        public void setSelectedIndex(int position) {
//            selectedIndex = position;
//            notifyDataSetChanged();
//        }
//
//        public BluetoothDevice getSelectedItem() {
//            return myList.get(selectedIndex);
//        }
//
//        @Override
//        public int getCount() {
//            return myList.size();
//        }
//
//        @Override
//        public BluetoothDevice getItem(int position) {
//            return myList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        private class ViewHolder {
//            TextView tv;
//        }
//
//        public void replaceItems(List<BluetoothDevice> list) {
//            myList = list;
//            notifyDataSetChanged();
//        }
//
//        public List<BluetoothDevice> getEntireList() {
//            return myList;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View vi = convertView;
//            ViewHolder holder;
//            if (convertView == null) {
//                vi = LayoutInflater.from(context).inflate(R.layout.list_item_reading, null);
//                holder = new ViewHolder();
//
//                holder.tv = (TextView) vi.findViewById(R.id.lstContent);
//
//                vi.setTag(holder);
//            } else {
//                holder = (ViewHolder) vi.getTag();
//            }
//
//            if (selectedIndex != -1 && position == selectedIndex) {
//                holder.tv.setBackgroundColor(selectedColor);
//            } else {
//                holder.tv.setBackgroundColor(Color.WHITE);
//            }
//            BluetoothDevice device = myList.get(position);
//            holder.tv.setText(device.getName() + "\n " + device.getAddress());
//
//            return vi;
//        }
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//// Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.homescreen, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Intent intent = new Intent(PlantInfoActivity.this, PreferencesActivity.class);
//                startActivityForResult(intent, SETTINGS);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
