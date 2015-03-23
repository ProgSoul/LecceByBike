package progsoul.opendata.leccebybike.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import progsoul.opendata.leccebybike.entities.BikeSharingStation;

/**
 * Created by ProgSoul on 14/03/2015.
 */
public class BikeSharingStationsSharedPreferences {
    public static void saveBikeSharingStations(Context context, ArrayList<BikeSharingStation> bikeSharingStations) {
        Gson gson = new Gson();
        String bikeSharingStationsJSON = gson.toJson(bikeSharingStations);

        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.BIKE_SHARING_STATIONS_LIST, bikeSharingStationsJSON);
        editor.commit();
    }

    public static ArrayList<BikeSharingStation> getSavedBikeSharingStations(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);

        ArrayList<BikeSharingStation> bikeSharingStations = null;
        try {
            String bikeSharingStationsJSON = sharedPreferences.getString(Constants.BIKE_SHARING_STATIONS_LIST, null);
            Gson gson = new Gson();
            bikeSharingStations = gson.fromJson(bikeSharingStationsJSON, new TypeToken<ArrayList<BikeSharingStation>>() {}.getType());
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }

        return bikeSharingStations;
    }
}
