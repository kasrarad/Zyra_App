package com.example.zyra;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
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
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class BlueToothTestActivity extends AppCompatActivity {


    protected Button ledToggleButton;
    protected TextView blueToothTextView;
    protected TextView bluetoothReadTextView;

    private InputStream inStream = null;
    boolean stopWorker = false;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];
    Handler handler = new Handler();

    private static final String TAG = "Bluetooth";
    private StringBuilder recDataString = new StringBuilder();
    String address = null , name = null;

    //initialize bluetooth
    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothtest);

//        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();

        Log.d(TAG, "Blueooth activity");
        try{setw();}catch(Exception e){}

    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setw() throws IOException
    {
        blueToothTextView=(TextView)findViewById(R.id.blueToothTextView);
        bluetoothReadTextView=(TextView)findViewById(R.id.bluetoothReadTextView);
        bluetooth_connect_device();


        ledToggleButton=(Button)findViewById(R.id.ledToggleButton);

        ledToggleButton.setOnTouchListener(new View.OnTouchListener()
        {   @Override
        public boolean onTouch(View v, MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_DOWN) {led_on_off("1");}
            if(event.getAction() == MotionEvent.ACTION_UP){led_on_off("0");}
            return true;}
        });
        listenForData();

    }

    private void bluetooth_connect_device() throws IOException
    {
        try
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
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

    public void listenForData()   {
        try {
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
        }

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == 10)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {

                                            bluetoothReadTextView.setText(data);
//                                            if(Result.getText().toString().equals("..")) {
//                                                Result.setText(data);
//                                            } else {
//                                                Result.append("\n"+data);
//                                            }

                                            /* You also can use Result.setText(data); it won't display multilines
                                             */

                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

}
