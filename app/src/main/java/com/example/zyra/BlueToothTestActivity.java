package com.example.zyra;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BlueToothTestActivity extends AppCompatActivity {


    protected Button ledToggleButton;
    protected TextView blueToothTextView;

    private static final String TAG = "Bluetooth";

    String address = null , name = null;

    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothtest);

//        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();

        Log.d(TAG, "HEre1 fsjhfhsdgfhjdsgfhjgfhjdsgjfkdsjfdhfdsl");
        try{setw();}catch(Exception e){}

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setw() throws IOException
    {
        blueToothTextView=(TextView)findViewById(R.id.blueToothTextView);
        bluetooth_connect_device();



        ledToggleButton=(Button)findViewById(R.id.ledToggleButton);

        ledToggleButton.setOnTouchListener(new View.OnTouchListener()
        {   @Override
        public boolean onTouch(View v, MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_DOWN) {led_on_off("0");}
            if(event.getAction() == MotionEvent.ACTION_UP){led_on_off("1");}
            return true;}
        });

    }

    private void bluetooth_connect_device() throws IOException
    {
        try
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            Log.d(TAG, "HEre1:");
            if (pairedDevices.size()>0)
            {
                for(BluetoothDevice bt : pairedDevices)
                {
                    address=bt.getAddress().toString();name = bt.getName().toString();
                    Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();

                }
            }

        }
        catch(Exception we){}
        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        btSocket.connect();
        try { blueToothTextView.setText("BT Name: "+name+"\nBT Address: "+address); }
        catch(Exception e){}
    }

//    @Override
//    public void onClick(View v)
//    {
//        try
//        {
//
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//
//    }

    private void led_on_off(String i)
    {
        try
        {
            if (btSocket!=null)
            {

                btSocket.getOutputStream().write(i.toString().getBytes());
            }

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

}
