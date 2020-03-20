package com.example.zyra.PlantsListView;

import android.content.Context;
import android.content.Intent;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.zyra.EditPlantActivity;
import com.example.zyra.R;

import java.util.ArrayList;

public class PlantListViewAdapter extends ArrayAdapter<String> {

    ArrayList<String> plantsName;
    Context context;

    public PlantListViewAdapter(@NonNull Context context, ArrayList<String> plantsName) {
        super(context, R.layout.plant_list_item);
        this.plantsName = plantsName;
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

            //viewHolder.imageDeletePlant = (ImageView) convertView.findViewById(R.id.imageDeletePlant);
            viewHolder.EditPlantButton = (Button) convertView.findViewById(R.id.EditPlantButton);
            viewHolder.textViewPlantName = (TextView) convertView.findViewById(R.id.textViewPlantName);

            viewHolder.EditPlantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((PlantActivity)context).temp();
                    Intent intent = new Intent(context, EditPlantActivity.class);
                    // send the value(plant's name)
                    intent.putExtra("nameByUser", plantsName.get(position));
                    context.startActivity(intent);
                    //Toast.makeText(getContext(), "Button was clicked for list item" + plantsName.get(position), Toast.LENGTH_SHORT).show();
                }
            });
/*
            viewHolder.imageDeletePlant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Image was clicked for list item" + position, Toast.LENGTH_SHORT).show();
                }
            });

 */
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewPlantName.setText(plantsName.get(position));

        return convertView;
    }
}
