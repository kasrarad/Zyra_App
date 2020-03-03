package com.example.zyra.SensorRecycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zyra.R;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {
    private ArrayList<Items> mItemsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewSensorA;

        public ItemsViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            mTextViewSensorA = itemView.findViewById(R.id.textViewSensorA);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!= null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ItemsAdapter(ArrayList<Items> itemsList){
        mItemsList = itemsList;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_items, parent, false);
        ItemsViewHolder ivh = new ItemsViewHolder(v, mListener);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        Items currentItem = mItemsList.get(position);

        holder.mTextViewSensorA.setText(currentItem.getSensorA());

    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }
}
