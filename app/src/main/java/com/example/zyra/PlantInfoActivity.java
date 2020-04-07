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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.example.zyra.Bluetooth.MonitoringScreen;
import com.example.zyra.Bluetooth.PreferencesActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlantInfoActivity extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series1;
    protected ImageButton btnImage;
    protected TextView textMyPlantName;
    protected TextView textMyPlantType;
    protected CircleImageView circleImgPlant;
    protected Button btnConfirm;
    String plantName;
    String plantSpecies;
    String plantPreviousMoisture;

    private ProgressDialog progressDialog;

//    private Button btnSearch;
//    private Button btnConnect;
//    private ListView listView;
//    private BluetoothAdapter mBTAdapter;
//    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
//    private static final int SETTINGS = 20;
//    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private int mBufferSize = 50000; //Default
//    public static final String DEVICE_EXTRA = "com.example.bluetoothtest.SOCKET";
//    public static final String DEVICE_UUID = "com.example.bluetoothtest.uuid";
//    private static final String DEVICE_LIST = "com.example.bluetoothtest.devicelist";
//    private static final String DEVICE_LIST_SELECTED = "com.example.bluetoothtest.devicelistselected";
//    public static final String BUFFER_SIZE = "com.example.bluetoothtest.buffersize";
//    private static final String TAG = "BlueTest5-MainActivity";

    protected double x,y;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantinfo);
        setupUI();


        // get plant's name
        plantName = getIntent().getStringExtra("nameByUser");
        System.out.println("nameByUser: " + plantName);
        textMyPlantName.setText(plantName);


        plantSpecies = getIntent().getStringExtra("nameBySpecies");
        textMyPlantType.setText(plantSpecies);

        System.out.println("Plant Species:" + plantSpecies);

        plantPreviousMoisture = getIntent().getStringExtra("previousMoisture");
        System.out.println("Plant Moisture: " + plantPreviousMoisture);

        if(plantPreviousMoisture.charAt(0) == '['){

            plantPreviousMoisture = "000000000000000000000000000";
        }
        System.out.println("Plant Moisture: " + plantPreviousMoisture);


        setGraph();


        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });


//        if (savedInstanceState != null) {
//            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
//            if (list != null) {
//                initList(list);
//                MyAdapter adapter = (MyAdapter) listView.getAdapter();
//                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
//                if (selectedIndex != -1) {
//                    adapter.setSelectedIndex(selectedIndex);
//                    btnConnect.setEnabled(true);
//                }
//            } else {
//                initList(new ArrayList<BluetoothDevice>());
//            }
//
//        } else {
//            initList(new ArrayList<BluetoothDevice>());
//        }
//        btnSearch.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                mBTAdapter = BluetoothAdapter.getDefaultAdapter();
//
//                if (mBTAdapter == null) {
//                    Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
//                } else if (!mBTAdapter.isEnabled()) {
//                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);
//                } else {
//                    new SearchDevices().execute();
//                }
//            }
//        });
//
//        btnConnect.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                BluetoothDevice device = ((MyAdapter) (listView.getAdapter())).getSelectedItem();
//                Intent intent = new Intent(getApplicationContext(), MonitoringScreen.class);
//                intent.putExtra(DEVICE_EXTRA, device);
//                intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
//                intent.putExtra(BUFFER_SIZE, mBufferSize);
//                startActivity(intent);
//            }
//        });

    }

    private void pickImageFromGallery() {
        //intent to pick plant image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void checkPermission() {
        Dexter.withActivity(PlantInfoActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(PlantInfoActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlantInfoActivity.this);
                            builder.setTitle("Permission Required")
                                    .setMessage("Permission to access gallery is required to choose a plant image." +
                                            "Please go to settings to enable storage permission. ")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, 100);
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }



    //handle result of picked image
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final Uri resultUri = result.getUri();
                circleImgPlant.setImageURI(resultUri);
                btnConfirm.setVisibility(View.VISIBLE);

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File imageFile = new File(resultUri.getPath());
                        progressDialog.show();

                        //would make it "fold" and it will not only be invisible but also won't take up space in the layuout either
                        btnConfirm.setVisibility(View.GONE);

                        AndroidNetworking.upload("http://zyraproject.ca/insertimage.php")
                                .addMultipartFile("image", imageFile)
                                .addMultipartParameter("userId", String.valueOf(11))
                                .setPriority(Priority.HIGH)
                                .build()
                                .setUploadProgressListener(new UploadProgressListener() {
                                    @Override
                                    public void onProgress(long bytesUploaded, long totalBytes) {
                                        float progress = (float) bytesUploaded/totalBytes * 100;
                                        progressDialog.setProgress((int) progress);
                                    }
                                })
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            progressDialog.dismiss();
                                            JSONObject jsonObject = new JSONObject(response);
                                            int status = jsonObject.getInt("status");
                                            String message = jsonObject.getString("message");
                                            if(status == 0) {
                                                Toast.makeText(PlantInfoActivity.this, "Unable to upload image" + message,
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PlantInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        anError.printStackTrace();
                                        Toast.makeText(PlantInfoActivity.this,
                                                "Error Uploading Image", Toast.LENGTH_SHORT);
                                    }
                                });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

//        switch (requestCode) {
//            case BT_ENABLE_REQUEST:
//                if (resultCode == RESULT_OK) {
//                    msg("Bluetooth Enabled successfully");
//                    new SearchDevices().execute();
//                } else {
//                    msg("Bluetooth couldn't be enabled");
//                }
//
//                break;
//            case SETTINGS: //If the settings have been updated
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//                String uuid = prefs.getString("prefUuid", "Null");
//                mDeviceUUID = UUID.fromString(uuid);
//                Log.d(TAG, "UUID: " + uuid);
//                String bufSize = prefs.getString("prefTextBuffer", "Null");
//                mBufferSize = Integer.parseInt(bufSize);
//                break;
//            default:
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setupUI() {
        textMyPlantName = findViewById(R.id.textViewPlantName);
        textMyPlantType = findViewById(R.id.textViewPlantType);
        btnConfirm = findViewById(R.id.buttonConfirm);
        btnImage = findViewById(R.id.buttonImage);
//        btnSearch = findViewById(R.id.search);
//        btnConnect = findViewById(R.id.connect);
        circleImgPlant = findViewById(R.id.imagePlant);

        progressDialog = new ProgressDialog(PlantInfoActivity.this);
        progressDialog.setMessage("Uploading Image . . .");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//
//        listView = findViewById(R.id.listview);
    }

    public void setGraph() {
        GraphView graph = findViewById(R.id.graph);
        graph.setTitle("Moisture Level");
        graph.getGridLabelRenderer();
        graph.getViewport().setMaxX(24);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setMinY(0);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        x = 0;
        series1 = new LineGraphSeries<>();

        int numDataPoint = 24;
        for(int i = 0; i < numDataPoint; i = i + 2){


            String value ="00";
            StringBuilder number = new StringBuilder("00");

            number.setCharAt(0, plantPreviousMoisture.charAt(i));
            number.setCharAt(1,plantPreviousMoisture.charAt(i + 1));


            value = number.toString();

//            System.out.println("Time: " +  i + "   Y: " + number +" Double check: " + value);

            x = i;
            y = Integer.parseInt(value);
            series1.appendData(new DataPoint(x,y),true,100);
        }
        graph.addSeries(series1);
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
