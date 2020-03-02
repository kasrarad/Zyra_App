package com.example.zyra;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zyra.PlantsRecycler.PlantsAdapter;
import com.example.zyra.PlantsRecycler.PlantsItem;

import java.util.ArrayList;

public class PlantActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PlantsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<PlantsItem> mPlantslist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantlist);

        createPlantList();
        buildRecyclerView();

    }


    public void removeItem(int position){
        mPlantslist.remove(position);
        mAdapter.notifyItemChanged(position);
    }


    public void createPlantList(){
        mPlantslist = new ArrayList<>();
        mPlantslist.add(new PlantsItem("Line 1", "Line 2"));
        mPlantslist.add(new PlantsItem("Line 3", "Line 4"));
        mPlantslist.add(new PlantsItem("Line 5", "Line 6"));
    }

    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerViewPlantList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new PlantsAdapter(mPlantslist);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new PlantsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                changeItem(position, "Clicked");
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }



}
