package progsoul.opendata.leccebybike.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;
import java.util.Random;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.entities.BikeSharingStation;
import progsoul.opendata.leccebybike.interfaces.AsyncRetrieveBikeSharingStationsTaskResponse;
import progsoul.opendata.leccebybike.tasks.RetrieveBikeSharingStationsTask;
import progsoul.opendata.leccebybike.utils.CustomSharedPreferences;
import progsoul.opendata.leccebybike.utils.Constants;

public class SplashActivity extends Activity implements AsyncRetrieveBikeSharingStationsTaskResponse {
    private Integer[] splashLogoDrawableResources = {
            R.drawable.logo_01,
            R.drawable.logo_02,
            R.drawable.logo_03,
            R.drawable.logo_04,
            R.drawable.logo_05,
            R.drawable.logo_06,
            R.drawable.logo_07,
            R.drawable.logo_08
    };
    private Random randomGenerator;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        randomGenerator = new Random();
        ImageView splashMainLogoImageView = (ImageView) findViewById(R.id.splashMainLogo);
        splashMainLogoImageView.setBackgroundResource(splashLogoDrawableResources[randomGenerator.nextInt(splashLogoDrawableResources.length)]);

        NumberProgressBar progressBar = (NumberProgressBar) findViewById(R.id.activity_splash_screen_progressBar);
        // calls the PHP script which retrieves informations about
        // Lecce's Bike Sharing Stations
        new RetrieveBikeSharingStationsTask(progressBar, this).execute();
    }

    @Override
    public void onAsyncTaskCompleted(ArrayList<BikeSharingStation> bikeSharingStations) {
        if (bikeSharingStations != null && !bikeSharingStations.isEmpty()) {
            CustomSharedPreferences.saveBikeSharingStations(this, bikeSharingStations);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else
            Toast.makeText(context, Constants.CANT_RETRIEVE_STATIONS_ERROR, Toast.LENGTH_LONG).show();

        finish();
    }
}