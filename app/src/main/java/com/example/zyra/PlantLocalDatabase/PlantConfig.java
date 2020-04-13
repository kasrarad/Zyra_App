package com.example.zyra.PlantLocalDatabase;

public class PlantConfig {

    public static final int SYNC_STATUS_OK = 0;
    public static final int SYNC_STATUS_FAILED = 1;
    public static final String UI_UPDATE_BROADCAST = "com.example.zyra.uiupdatebroadcast";

    // Database Name
    public static final String DATABASE_NAME = "plantsDB";

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String PLANT_TABLE_NAME = "plant";

    // column names
    public static final String COLUMN_KEY_ID = "_id";
    public static final String COLUMN_USERID = "userID";
    public static final String COLUMN_NAMEBYSPECIES = "nameBySpecies";
    public static final String COLUMN_NAMEBYUSER = "nameByUser";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_MOISTURE = "moisture";
    public static final String COLUMN_PREVIOUSMOISTURESLEVEL = "previousMoisturesLevel";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_WIKI = "wiki";
    public static final String SYNC_STATUS = "syncstatus";

}
