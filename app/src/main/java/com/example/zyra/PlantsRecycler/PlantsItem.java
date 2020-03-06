package com.example.zyra.PlantsRecycler;

public class PlantsItem {
    private String mTextPlantName;
    private String mTextPlantType;

    public PlantsItem(String mTextPlantName, String mTextPlantType) {
        this.mTextPlantName = mTextPlantName;
        this.mTextPlantType = mTextPlantType;
    }


    public String getmTextPlantName() {
        return mTextPlantName;
    }

    public String getmTextPlantType() {
        return mTextPlantType;
    }
}
