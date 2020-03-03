package com.example.zyra.DatabaseDummy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.zyra.Plants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = Config.DATABASE_NAME;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLANTS_TABLE = "CREATE TABLE " + Config.TABLE_PLANTS_NAME + " ("
                + Config.COLUMN_PLANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_PLANT_TITLE + " TEXT NOT NULL,"
                + Config.COLUMN_PLANT_TYPE + " TEXT NOT NULL)";

        Log.d(TAG, "Table create SQL: " + CREATE_PLANTS_TABLE);
        db.execSQL(CREATE_PLANTS_TABLE);
        Log.d(TAG, "DB Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long insertPlant(Plants plants) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_PLANT_TITLE, plants.getPlantTitle());
        contentValues.put(Config.COLUMN_PLANT_TYPE, plants.getPlantType());

        //try putting course in the table, if there's an error, catch it and display it
        try {
            id = db.insertOrThrow(Config.TABLE_PLANTS_NAME, null, contentValues);
        } catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }

        return id;
    }

    public List<Plants> getAllPlants() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Config.TABLE_PLANTS_NAME, null, null, null, null, null, null, null);
            if (cursor != null)
                if (cursor.moveToFirst()) {
                    List<Plants> plantList = new ArrayList<>();
                    do {
                        int ID = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_PLANT_ID));
                        String Title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PLANT_TITLE));
                        String Type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PLANT_TYPE));

                        plantList.add(new Plants(ID, Title, Type));
                    } while (cursor.moveToNext());

                    return plantList;
                }
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return Collections.emptyList();
    }

}