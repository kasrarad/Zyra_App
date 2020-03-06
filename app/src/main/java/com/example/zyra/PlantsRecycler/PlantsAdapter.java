package com.example.zyra.PlantsRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zyra.R;

import java.util.ArrayList;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantsViewHolder> {

    private ArrayList<PlantsItem> mPlantsList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class PlantsViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewPlantName;
        public TextView mTextViewPlantType;
        public ImageView mDeleteImage;

        public PlantsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            mTextViewPlantName = itemView.findViewById(R.id.textViewPlantName);
            mTextViewPlantType = itemView.findViewById(R.id.textViewPlantType);
            mDeleteImage = itemView.findViewById(R.id.imageDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                }
            });

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public PlantsAdapter(ArrayList<PlantsItem> plantsList){
        mPlantsList = plantsList;
    }

    @NonNull
    @Override
    public PlantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_item, parent, false);
        PlantsViewHolder pvh = new PlantsViewHolder(v, mListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlantsViewHolder holder, int position) {
        PlantsItem currentItem = mPlantsList.get(position);

//        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.mTextViewPlantName.setText(currentItem.getmTextPlantName());
        holder.mTextViewPlantType.setText(currentItem.getmTextPlantType());
    }

    @Override
    public int getItemCount() {
        return mPlantsList.size();
    }
}
