package com.example.zyra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.zyra.Database.SyncPlants;
import com.example.zyra.PlantLocalDatabase.PlantConfig;
import com.example.zyra.PlantLocalDatabase.PlantDbHelper;
import com.example.zyra.PlantsListView.PlantListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class NetworkMonitor extends BroadcastReceiver {

    protected ArrayList<String> plantId;
    protected ArrayList<String> userID;
    protected ArrayList<String> nameBySpecies;
    protected ArrayList<String> nameByUser;
    protected ArrayList<String> temperature;
    protected ArrayList<String> moisture;
    protected ArrayList<Integer> previousMoisturesLevel;
    protected ArrayList<String> image;
    protected ArrayList<String> wiki;
    protected ArrayList<Integer> plantSyncStatus;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(checkNetworkConnection(context)){

            PlantDbHelper plantDbHelper = new PlantDbHelper(context);
            List<PlantInfoDB> plants = plantDbHelper.readFromLocalDatabase();

            plantId = new ArrayList<>();
            userID = new ArrayList<>();
            nameBySpecies = new ArrayList<>();
            nameByUser = new ArrayList<>();
            temperature = new ArrayList<>();
            moisture = new ArrayList<>();
            previousMoisturesLevel = new ArrayList<>();
            image = new ArrayList<>();
            wiki = new ArrayList<>();
            plantSyncStatus = new ArrayList<>();

            for (int i=0; i<plants.size(); i++){
                if(plants.get(i).getSyncstatus() == PlantConfig.SYNC_STATUS_FAILED){
                    Integer plantId = plants.get(i).getID();
                    String userId = plants.get(i).getUserID();
                    String nameBySpecies = plants.get(i).getNameBySpecies();
                    String nameByUser = plants.get(i).getNameByUser();
                    String temperature = plants.get(i).getTemperature();
                    String moisture = plants.get(i).getMoisture();
                    String previousMoisturesLevel = plants.get(i).getPreviousMoisturesLevel();
                    String image = plants.get(i).getImage();
                    String wiki = plants.get(i).getWiki();
                    SyncPlants syncPlants = new SyncPlants(context);
                    syncPlants.execute(userId, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki);
                    //context.sendBroadcast(new Intent(PlantConfig.UI_UPDATE_BROADCAST));
                    Intent intent1 = new Intent(context, PlantActivity.class);
                    context.startActivity(intent1);
                    PlantInfoDB plantInfoDB = new PlantInfoDB(plantId, userId, nameBySpecies, nameByUser, temperature, moisture, previousMoisturesLevel, image, wiki, PlantConfig.SYNC_STATUS_OK);
                    plantDbHelper.updateLocalDatabase(plantInfoDB);
                }
            }

            plantDbHelper.close();
        }
    }

    public boolean checkNetworkConnection(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());

    }
}
