package com.example.zyra;

public class Items {
    private String mSensorA;

    public Items(String sensorA) {
        mSensorA = sensorA;

    }

    public void changeSensorA(String text){
        mSensorA = text;
    }

    public String getSensorA() {
        return mSensorA;
    }

}
