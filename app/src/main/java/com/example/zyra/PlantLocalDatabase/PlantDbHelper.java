package com.example.zyra.PlantLocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.example.zyra.LocalDatabase.Config;
import com.example.zyra.PlantInfoDB;
import com.example.zyra.UserInfoDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlantDbHelper extends SQLiteOpenHelper {

    private static final String DROP_TABLE = "drop table if exists " + PlantConfig.PLANT_TABLE_NAME;

    // Table Create Statements
    private static final String CREATE_TABLE_PLANT = "CREATE TABLE " + PlantConfig.PLANT_TABLE_NAME +
            " (" + PlantConfig.COLUMN_KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + PlantConfig.COLUMN_USERID + " TEXT,"
            + PlantConfig.COLUMN_NAMEBYSPECIES + " TEXT,"
            + PlantConfig.COLUMN_NAMEBYUSER + " TEXT,"
            + PlantConfig.COLUMN_TEMPERATURE + " TEXT,"
            + PlantConfig.COLUMN_MOISTURE + " TEXT,"
            + PlantConfig.COLUMN_PREVIOUSMOISTURESLEVEL + " TEXT,"
            + PlantConfig.COLUMN_IMAGE + " TEXT,"
            + PlantConfig.COLUMN_WIKI + " TEXT,"
            + PlantConfig.SYNC_STATUS + " INTEGER" + ")";

    private Context context;
    private static final String TAG = "PlantDbHelper";

    public PlantDbHelper(Context context){
        super(context, PlantConfig.DATABASE_NAME, null, PlantConfig.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_PLANT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL(DROP_TABLE);
        onCreate(db);

    }

    public long saveToLocalDatabase(PlantInfoDB plantInfoDB){

        long id = -1;
        int check = 0;
        SQLiteDatabase database = this.getWritableDatabase();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(PlantConfig.PLANT_TABLE_NAME, null,null,null,null,null,null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        String nameByUser = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_NAMEBYUSER));
                        if(nameByUser.equals(plantInfoDB.getNameByUser())){
                            check = 1;
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        if(check == 0){
            ContentValues contentValues = new ContentValues();

            contentValues.put(PlantConfig.COLUMN_USERID, plantInfoDB.getUserID());
            contentValues.put(PlantConfig.COLUMN_NAMEBYSPECIES, plantInfoDB.getNameBySpecies());
            contentValues.put(PlantConfig.COLUMN_NAMEBYUSER, plantInfoDB.getNameByUser());
            contentValues.put(PlantConfig.COLUMN_TEMPERATURE, plantInfoDB.getTemperature());
            contentValues.put(PlantConfig.COLUMN_MOISTURE, plantInfoDB.getMoisture());
            contentValues.put(PlantConfig.COLUMN_PREVIOUSMOISTURESLEVEL, plantInfoDB.getPreviousMoisturesLevel());
            contentValues.put(PlantConfig.COLUMN_IMAGE, plantInfoDB.getImage());
            contentValues.put(PlantConfig.COLUMN_WIKI, plantInfoDB.getWiki());
            contentValues.put(PlantConfig.SYNC_STATUS, plantInfoDB.getSyncstatus());

            try {
                id = database.insertOrThrow(PlantConfig.PLANT_TABLE_NAME, null, contentValues);
            } catch (SQLiteException e){
                Log.d(TAG, "EXCEPTION: " + e);
                Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(context, "Plant Name Already Existed ", Toast.LENGTH_LONG).show();
        }

        return id;
    }

    public List<PlantInfoDB> readFromLocalDatabase(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(PlantConfig.PLANT_TABLE_NAME, null,null,null,null,null,null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    // Go through every row of the data and retrieve the data from it
                    List<PlantInfoDB> plantInfoDBS = new ArrayList<>();

                    do {
                        // Retrieve the row in each iteration
                        int id = cursor.getInt(cursor.getColumnIndex(PlantConfig.COLUMN_KEY_ID));
                        String userID = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_USERID));
                        String nameBySpecies = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_NAMEBYSPECIES));
                        String nameByUser = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_NAMEBYUSER));
                        String temperature = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_TEMPERATURE));
                        String moisture = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_MOISTURE));
                        String previousMoisturesLevel = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_PREVIOUSMOISTURESLEVEL));
                        String image = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_IMAGE));
                        String wiki = cursor.getString(cursor.getColumnIndex(PlantConfig.COLUMN_WIKI));
                        Integer syncstatus = cursor.getInt(cursor.getColumnIndex(PlantConfig.SYNC_STATUS));

                        plantInfoDBS.add(new PlantInfoDB(id, userID, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki, syncstatus));

                    } while (cursor.moveToNext());

                    return plantInfoDBS;
                }
            }
        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return Collections.emptyList();

    }

    public void updateLocalDatabase(PlantInfoDB plantInfoDB){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        //contentValues.put(PlantConfig.COLUMN_KEY_ID, plantInfoDB.getUserID());
        contentValues.put(PlantConfig.COLUMN_USERID, plantInfoDB.getUserID());
        contentValues.put(PlantConfig.COLUMN_NAMEBYSPECIES, plantInfoDB.getNameBySpecies());
        contentValues.put(PlantConfig.COLUMN_NAMEBYUSER, plantInfoDB.getNameByUser());
        contentValues.put(PlantConfig.COLUMN_TEMPERATURE, plantInfoDB.getTemperature());
        contentValues.put(PlantConfig.COLUMN_MOISTURE, plantInfoDB.getMoisture());
        contentValues.put(PlantConfig.COLUMN_PREVIOUSMOISTURESLEVEL, plantInfoDB.getPreviousMoisturesLevel());
        contentValues.put(PlantConfig.COLUMN_IMAGE, plantInfoDB.getImage());
        contentValues.put(PlantConfig.COLUMN_WIKI, plantInfoDB.getWiki());
        contentValues.put(PlantConfig.SYNC_STATUS, plantInfoDB.getSyncstatus());

        // updating row
        db.update(PlantConfig.PLANT_TABLE_NAME, contentValues,  PlantConfig.COLUMN_KEY_ID + " = ?",new String[] { plantInfoDB.getID().toString() });

    }

    // delete user
    public Integer deletePlant(String nameByUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer temp = 0;

        try {

            temp = db.delete(PlantConfig.PLANT_TABLE_NAME, PlantConfig.COLUMN_NAMEBYUSER + " = ?", new String[] { nameByUser });

        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }

        return temp;

    }

    // delete user
    public Integer deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer temp = 0;

        try {
            db.execSQL("delete from "+ PlantConfig.PLANT_TABLE_NAME);
        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }

        return temp;

    }
}
