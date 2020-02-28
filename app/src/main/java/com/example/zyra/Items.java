package com.example.zyra;

public class Items {
    private String mSensorA;
    private String mSensorB;
    private String mSensorC;

    public Items(String sensorA, String sensorB, String sensorC) {
        mSensorA = sensorA;
        mSensorB = sensorB;
        mSensorC = sensorC;
    }

    public String getSensorA() {
        return mSensorA;
    }

    public String getSensorB() {
        return mSensorB;
    }

    public String getSensorC() {
        return mSensorC;
    }
}
