package com.example.zyra;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUser {

    public static final String sharedPreferencesName = "userDetails";
    SharedPreferences userLocalDatabase;

    public SharedPreferencesUser(Context context){
        userLocalDatabase = context.getSharedPreferences(sharedPreferencesName, 0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor sharedPreferencesEditor = userLocalDatabase.edit();
        sharedPreferencesEditor.putString("full name", user.fullName);
        sharedPreferencesEditor.putString("username", user.username);
        sharedPreferencesEditor.putString("password", user.password);
        sharedPreferencesEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDatabase.getString("name", "");
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password", "");

        User storedUser = new User(name, username, password);

        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor sharedPreferencesEditor = userLocalDatabase.edit();
        sharedPreferencesEditor.putBoolean("loggedIn", loggedIn);
        sharedPreferencesEditor.commit();
    }

    public void clearUserData(){
        SharedPreferences.Editor sharedPreferencesEditor = userLocalDatabase.edit();
        sharedPreferencesEditor.clear();
        sharedPreferencesEditor.commit();
    }


    public boolean getUserLoggedIn(){
        if(userLocalDatabase.getBoolean("loggedIn", false) == true){
            return true;
        }else{
            return false;
        }
    }
}
