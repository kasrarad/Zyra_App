package com.example.zyra;

public class Items {
    private String mSensorA;
    private String mSensorB;

    public Items(String sensorA, String sensorB) {
        mSensorA = sensorA;
        mSensorB  = sensorB;
    }

    public void changeSensorA(String text){
        mSensorA = text;
    }

    public void changeSensorB(String text){
        mSensorB = text;
    }

    public String getSensorA() {
        return mSensorA;
    }

    public String getSensorB() {
        return mSensorB;
    }
}
