package com.example.zyra;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MoistureData {

    public int currentMoistureLevel;
    public List<Integer> previousMoistureLevels;
    public String moistureData;

    public MoistureData(){
        currentMoistureLevel = 0;
        previousMoistureLevels = new ArrayList<>();
        for (int i=0; i<12; i++){
            previousMoistureLevels.add(0);
        }
    }

    public void addMoistureData(){
        currentMoistureLevel = 20;
        previousMoistureLevels = new ArrayList<>();
        for (int i=0; i<12; i++){
            previousMoistureLevels.add(i);
        }
    }

    public String getCurrentMoistureLevel(){
        return String.valueOf(currentMoistureLevel);
    }

    public String getPreviousMoistureLevels() {
        Gson gson = new Gson();
        moistureData = gson.toJson(previousMoistureLevels);
        return moistureData;
    }

}
