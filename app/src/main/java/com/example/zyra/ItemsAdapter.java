package com.example.zyra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> {
    private ArrayList<Items> mItemsList;

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewSensorA;
        public TextView mTextViewSensorB;
        public TextView mTextViewSensorC;

        public ItemsViewHolder(View itemView){
            super(itemView);
            mTextViewSensorA = itemView.findViewById(R.id.textViewSensorA);
            mTextViewSensorB = itemView.findViewById(R.id.textViewSensorB);
            mTextViewSensorC = itemView.findViewById(R.id.textViewSensorC);
        }
    }

    public ItemsAdapter(ArrayList<Items> itemsList){
        mItemsList = itemsList;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_items, parent, false);
        ItemsViewHolder ivh = new ItemsViewHolder(v);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        Items currentItem = mItemsList.get(position);

        holder.mTextViewSensorA.setText(currentItem.getSensorA());
        holder.mTextViewSensorB.setText(currentItem.getSensorB());
        holder.mTextViewSensorC.setText(currentItem.getSensorC());
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }
}
