package com.example.zyra.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.zyra.UserInfoDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context){
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Table Create Statements
        // User table create statement
        String CREATE_TABLE_USER = "CREATE TABLE " + Config.USER_TABLE_NAME +
                " (" + Config.COLUMN_KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_USERID + " TEXT NOT NULL,"
                + Config.COLUMN_NAME + " TEXT NOT NULL,"
                + Config.COLUMN_USERNAME + " TEXT NOT NULL,"
                + Config.COLUMN_PASSWORD + " TEXT NOT NULL" + ")";

        Log.d(TAG, CREATE_TABLE_USER);

        db.execSQL(CREATE_TABLE_USER);

        Log.d(TAG, "User's database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertUserInfo(UserInfoDB userInfo){

        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.COLUMN_USERID, userInfo.getUserID());
        contentValues.put(Config.COLUMN_NAME, userInfo.getName());
        contentValues.put(Config.COLUMN_USERNAME, userInfo.getUsername());
        contentValues.put(Config.COLUMN_PASSWORD, userInfo.getPassword());

        try {
            id = db.insertOrThrow(Config.USER_TABLE_NAME, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }
        return id;
    }

    public List<UserInfoDB> getAllUserInfo(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(Config.USER_TABLE_NAME, null,null,null,null,null,null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    // Go through every row of the data and retrieve the data from it
                    List<UserInfoDB> userInfos = new ArrayList<>();

                    do {
                        // Retrieve the row in each iteration
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_KEY_ID));
                        String userID = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USERID));
                        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NAME));
                        String username = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USERNAME));
                        String password = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PASSWORD));

                        userInfos.add(new UserInfoDB(id, userID, name, username, password));

                    } while (cursor.moveToNext());

                    return userInfos;
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

    // delete user
    public Integer deleteUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer temp = 0;

        try {

            temp = db.delete(Config.USER_TABLE_NAME, Config.COLUMN_USERID + " = ?", new String[] { userId });

        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }

        return temp;

    }

}
