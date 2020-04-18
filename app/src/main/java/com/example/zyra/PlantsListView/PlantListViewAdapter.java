package com.example.zyra.PlantsListView;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.health.SystemHealthManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.zyra.Bluetooth.BluetoothActivity;
import com.example.zyra.EditPlantActivity;
import com.example.zyra.PlantActivity;
import com.example.zyra.PlantInfoActivity;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.Plants;
import com.example.zyra.R;

import java.util.ArrayList;

import com.example.zyra.MoistureActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlantListViewAdapter extends ArrayAdapter<String> {

    ArrayList<String> plantsText;
    ArrayList<Integer> plantsID;
    ArrayList<String> plantsName;
    static ArrayList<String> plantSpecies;
    static public ArrayList<String> plantsPreviousMoisture;
    static ArrayList<String> plantsImage;
    static ArrayList<Integer> plantSyncStatus;
    Context context;

    public PlantListViewAdapter(@NonNull Context context, ArrayList<String> plantsName) {
        super(context, R.layout.plant_list_item);
        this.plantsName = plantsName;
        this.context = context;
    }

    public PlantListViewAdapter(@NonNull Context context, ArrayList<String> plantsName, ArrayList<String> plantSpecies) {
        super(context, R.layout.plant_list_item);
        this.plantsName = plantsName;
        this.plantSpecies = plantSpecies;
        this.context = context;
    }

    public PlantListViewAdapter(@NonNull Context context, ArrayList<String> plantsName, ArrayList<String> plantSpecies, ArrayList<String> plantsPreviousMoisture, ArrayList<String> plantsImage) {
        super(context, R.layout.plant_list_item);
        this.plantsName = plantsName;
        this.plantSpecies = plantSpecies;
        this.plantsPreviousMoisture = plantsPreviousMoisture;
        this.plantsImage = plantsImage;
        this.context = context;
    }

    public PlantListViewAdapter(@NonNull Context context, ArrayList<String> plantsText, ArrayList<Integer> plantsID, ArrayList<String> plantsName, ArrayList<String> plantSpecies, ArrayList<String> plantsPreviousMoisture, ArrayList<String> plantsImage, ArrayList<Integer> plantSyncStatus) {
        super(context, R.layout.plant_list_item);
        this.plantsText = plantsText;
        this.plantsID = plantsID;
        this.plantsName = plantsName;
        this.plantSpecies = plantSpecies;
        this.plantsPreviousMoisture = plantsPreviousMoisture;
        this.plantsImage = plantsImage;
        this.plantSyncStatus = plantSyncStatus;
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantsName.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if(convertView == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.plant_list_item, parent,false);

//            viewHolder.moistureButton = (Button) convertView.findViewById(R.id.MoistureButton);
            viewHolder.EditPlantButton = (Button) convertView.findViewById(R.id.EditPlantButton);
            viewHolder.textViewPlantName = (TextView) convertView.findViewById(R.id.textViewPlantName);
            viewHolder.PlantBluetooth = (ImageButton) convertView.findViewById(R.id.imageBT);
            viewHolder.plantImg = (CircleImageView) convertView.findViewById((R.id.plantImg));
            viewHolder.plantSyncImg = (ImageView) convertView.findViewById(R.id.plantSyncImg);
//
//            System.out.println("PLANT SPECIES: " + plantSpecies);
//            System.out.println(" AT POSITION: " + plantSpecies.get(position));


//            viewHolder.moistureButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, MoistureActivity.class);
//                    // send the value(plant's name)
//                    intent.putExtra("nameByUser", plantsName.get(position));
//                    context.startActivity(intent);
//                }
//            });

            viewHolder.EditPlantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkNetworkConnection()){
                        Toast.makeText(context, "No internet Connection", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(context, EditPlantActivity.class);
                        // send the value(plant's name)
                        intent.putExtra("nameByUser", plantsName.get(position));
                        intent.putExtra("plantsID", plantsID.get(position).toString());
                        context.startActivity(intent);
                    }
                }
            });

            viewHolder.PlantBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BluetoothActivity.class);
                    intent.putExtra("plantID", Integer.toString(position));
                    intent.putExtra("nameByUser", plantsName.get(position));
                    context.startActivity(intent);
                }
            });

            String badPlantName = plantsText.get(position);
            final String[] plantNameSize = badPlantName.split("\n");
            String plantName = plantNameSize[0];
            plantNameSize[0].length();

            viewHolder.textViewPlantName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlantInfoActivity.class);
                    // send the value(plant's name)

                    intent.putExtra("plantID", Integer.toString(position));
                    System.out.println("PlantID: "+ Integer.toString(position));
                    intent.putExtra("nameByUser", plantsName.get(position));
                    intent.putExtra("nameBySpecies", plantSpecies.get(position));
                    intent.putExtra("previousMoisture", plantsPreviousMoisture.get(position));
                    intent.putExtra("image", plantsImage.get(position));
                    intent.putExtra( "currentMoisture", plantNameSize[1]);
                    context.startActivity(intent);
                }
            });

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

//
//        System.out.println("PlantText: " + plantsText.get(position));
        String badPlantName = plantsText.get(position);
        String[] plantNameSize = badPlantName.split("\n");
        String plantName = plantNameSize[0];
        plantNameSize[0].length();
//        System.out.println(plantName);


        SpannableString ss = new SpannableString(plantsText.get(position));

        ForegroundColorSpan fcsRed = new ForegroundColorSpan(0xF4D02020);
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(0XF42FA404);
        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(0XF40724FF);

        String moistureNumberOnly= plantNameSize[1].replaceAll("[^0-9]", "");



        if(Integer.parseInt(moistureNumberOnly) < 70){
            ss.setSpan(fcsGreen,plantNameSize[0].length() + 1, badPlantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        if(Integer.parseInt(moistureNumberOnly) < 20){
        ss.setSpan(fcsRed,plantNameSize[0].length() + 1, badPlantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        if(Integer.parseInt(moistureNumberOnly) > 69){
            ss.setSpan(fcsBlue,plantNameSize[0].length() + 1, badPlantName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        viewHolder.textViewPlantName.setText(ss);

        if(!plantsImage.get(position).equals(null)){
            Uri uri = Uri.parse(plantsImage.get(position));
            viewHolder.plantImg.setImageURI(uri);
        }
        int sync_status = plantSyncStatus.get(position);
        if(sync_status == PlantConfig.SYNC_STATUS_OK){
            viewHolder.plantSyncImg.setImageResource(R.drawable.check);
        } else {
            viewHolder.plantSyncImg.setImageResource(R.drawable.sync);
        }

        return convertView;
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }
}