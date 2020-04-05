package com.example.zyra.PlantsListView;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.zyra.Bluetooth.BluetoothActivity;
import com.example.zyra.EditPlantActivity;
import com.example.zyra.PlantInfoActivity;
import com.example.zyra.Plants;
import com.example.zyra.R;

import java.util.ArrayList;

import com.example.zyra.MoistureActivity;

public class PlantListViewAdapter extends ArrayAdapter<String> {

    ArrayList<String> plantsName;
    static ArrayList<String> plantSpecies;
    static public ArrayList<String> plantsPreviousMoisture;
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

    public PlantListViewAdapter(@NonNull Context context, ArrayList<String> plantsName, ArrayList<String> plantSpecies, ArrayList<String> plantsPreviousMoisture) {
        super(context, R.layout.plant_list_item);
        this.plantsName = plantsName;
        this.plantSpecies = plantSpecies;
        this.plantsPreviousMoisture = plantsPreviousMoisture;
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
                    Intent intent = new Intent(context, EditPlantActivity.class);
                    // send the value(plant's name)
                    intent.putExtra("nameByUser", plantsName.get(position));
                    context.startActivity(intent);
                }
            });

            viewHolder.PlantBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BluetoothActivity.class);
                    intent.putExtra("nameByUser", plantsName.get(position));
                    context.startActivity(intent);
                }
            });

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
                    context.startActivity(intent);
                }
            });

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewPlantName.setText(plantsName.get(position));

        return convertView;
    }
}