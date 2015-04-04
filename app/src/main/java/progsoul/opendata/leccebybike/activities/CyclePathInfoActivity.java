package progsoul.opendata.leccebybike.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.entities.CyclePath;
import progsoul.opendata.leccebybike.utils.Constants;
import progsoul.opendata.leccebybike.utils.GenericUtils;

public class CyclePathInfoActivity extends Activity implements OnMapReadyCallback {
    private CyclePath cyclePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_path_info);

        cyclePath = getIntent().getExtras().getParcelable(Constants.CYCLE_PATH);

        initializeLayoutViews();
    }

    private void initializeLayoutViews() {
        MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.cyclePathLocationMap);
        mapFragment.getMapAsync(this);

        Button actionBarBackButton = (Button) findViewById(R.id.title_bar_left_back);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(CyclePathInfoActivity.this, R.anim.click_animation));

                onBackPressed();
            }
        });

        TextView cyclePathNameTextView = (TextView) findViewById(R.id.cyclePathNameLabel);
        cyclePathNameTextView.setText(GenericUtils.ellipsize(cyclePath.getName(), 25));

        TextView cyclePathDescriptionTextView = (TextView) findViewById(R.id.expandable_text);
        cyclePathDescriptionTextView.setText(cyclePath.getDescription());

        CyclePath.Features features = cyclePath.getFeatures();

        TextView cyclePathTypeTextView = (TextView) findViewById(R.id.cyclePathTypeLabel);
        cyclePathTypeTextView.setText(features.getType().toString().toLowerCase());

        TextView cyclePathDistanceTextView = (TextView) findViewById(R.id.cyclePathDistanceLabel);
        cyclePathDistanceTextView.setText(features.getDistance());

        TextView cyclePathRoadSurfaceTextView = (TextView) findViewById(R.id.cyclePathRoadSurfaceLabel);
        cyclePathRoadSurfaceTextView.setText(features.getRoadSurface().toString().toLowerCase());

        if (features.isSuitableForChildren()) {
            TextView cyclePathSuitableForChildrenTextView = (TextView) findViewById(R.id.cyclePathSuitableForChildrenLabel);
            cyclePathSuitableForChildrenTextView.setText("si");
        }

        if (features.isSuitableForSkaters()) {
            TextView cyclePathSuitableForSkatersTextView = (TextView) findViewById(R.id.cyclePathSuitableForSkatersLabel);
            cyclePathSuitableForSkatersTextView.setText("si");
        }

        CyclePath.Details details = cyclePath.getDetails();

        TextView cyclePathAverageSlopeTextView = (TextView) findViewById(R.id.cyclePathAverageSlopeLabel);
        cyclePathAverageSlopeTextView.setText(details.getAverageSlope());

        TextView cyclePathMaxSlopeTextView = (TextView) findViewById(R.id.cyclePathMaxSlopeLabel);
        cyclePathMaxSlopeTextView.setText(details.getMaxSlope());

        TextView cyclePathTrackDensityTextView = (TextView) findViewById(R.id.cyclePathTrackDensityLabel);
        cyclePathTrackDensityTextView.setText(details.getTrackDensity());

        TextView cyclePathDifferenceTextView = (TextView) findViewById(R.id.cyclePathDifferenceLabel);
        cyclePathDifferenceTextView.setText(details.getDifference());

        TextView cyclePathAscentDifference = (TextView) findViewById(R.id.cyclePathAscentDifferenceLabel);
        cyclePathAscentDifference.setText(details.getAscentDifference());

        TextView cyclePathDescentDifference = (TextView) findViewById(R.id.cyclePathDescentDifferenceLabel);
        cyclePathDescentDifference.setText(details.getDescentDifference());

        TextView launchGoogleMapsTextView = (TextView) findViewById(R.id.launchGoogleMapsLabel);
        launchGoogleMapsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", cyclePath.getLatitudes()[0], cyclePath.getLongitudes()[0], cyclePath.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(getBaseContext(), "Impossibile avviare navigatore, installare Google Maps.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        PolylineOptions polylineOptions = new PolylineOptions();
        double[] latitudes = cyclePath.getLatitudes();
        double[] longitudes = cyclePath.getLongitudes();
        for (int i = 0; i < latitudes.length; i++)
            polylineOptions.add(new LatLng(latitudes[i], longitudes[i]));
        Pair<Integer, Float> polylineColor = GenericUtils.getColorBasedOnCyclePathType(cyclePath, getResources().getStringArray(R.array.colors_palette));
        polylineOptions.color(polylineColor.first);

        LatLng cyclePathBeginningCoordinates = new LatLng(latitudes[0], longitudes[0]);
        MarkerOptions markerOptions = new MarkerOptions()
                .title(cyclePath.getName())
                .position(cyclePathBeginningCoordinates)
                .icon(BitmapDescriptorFactory.defaultMarker(polylineColor.second));
        googleMap.addMarker(markerOptions);
        googleMap.addPolyline(polylineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cyclePathBeginningCoordinates, 13));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}