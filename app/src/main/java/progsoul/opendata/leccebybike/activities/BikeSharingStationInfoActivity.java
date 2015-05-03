package progsoul.opendata.leccebybike.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.entities.BikeSharingStation;
import progsoul.opendata.leccebybike.utils.Constants;
import progsoul.opendata.leccebybike.utils.GenericUtils;

public class BikeSharingStationInfoActivity extends Activity implements OnMapReadyCallback {
    private BikeSharingStation bikeSharingStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_sharing_station_info);

        bikeSharingStation = getIntent().getExtras().getParcelable(Constants.BIKE_SHARING_STATION);

        initializeLayoutViews();
    }

    private void initializeLayoutViews() {
        MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.bikeSharingLocationMap);
        mapFragment.getMapAsync(this);

        Button actionBarBackButton = (Button) findViewById(R.id.title_bar_left_back);
        actionBarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(BikeSharingStationInfoActivity.this, R.anim.click_animation));

                onBackPressed();
            }
        });

        TextView bikeSharingStationNameTextView = (TextView) findViewById(R.id.bikeSharingStationNameLabel);
        bikeSharingStationNameTextView.setText(GenericUtils.ellipsize(bikeSharingStation.getName(), 25));

        TextView bikeSharingStationAddressTextView = (TextView) findViewById(R.id.bikeSharingStationAddressLabel);
        bikeSharingStationAddressTextView.setText(GenericUtils.ellipsize(bikeSharingStation.getAddress(), 25));

        ImageView bikeSharingStationImageView = (ImageView) findViewById(R.id.bikeSharingStationImageView);
        Picasso.with(this)
                .load(bikeSharingStation.getImageURL())
                .into(bikeSharingStationImageView);
        bikeSharingStationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", bikeSharingStation.getLatitude(), bikeSharingStation.getLongitude(), bikeSharingStation.getName());
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

        TextView bikeSharingStationFreeBikesTextView = (TextView) findViewById(R.id.bikeSharingStationFreeBikesLabel);
        bikeSharingStationFreeBikesTextView.setText(bikeSharingStation.getFreeBikes() + "");

        TextView bikeSharingStationAvailablePlacesTextView = (TextView) findViewById(R.id.bikeSharingStationAvailablePlacesLabel);
        bikeSharingStationAvailablePlacesTextView.setText(bikeSharingStation.getAvailablePlaces() + "");

        if (!bikeSharingStation.isOperative()) {
            TextView bikeSharingStationStatusTextView = (TextView) findViewById(R.id.bikeSharingStationStatusLabel);
            bikeSharingStationStatusTextView.setText("NON ATTIVA");
            bikeSharingStationStatusTextView.setTextColor(getResources().getColor(R.color.Red));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //if bike station is operative, then color marker will be green, else red
        int markerResourceId = bikeSharingStation.isOperative() ? R.drawable.marker_attiva : R.drawable.marker_nonattiva;
        LatLng bikeSharingStationCoordinates = new LatLng(bikeSharingStation.getLatitude(), bikeSharingStation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .title(bikeSharingStation.getName())
                .position(bikeSharingStationCoordinates)
                .icon(BitmapDescriptorFactory.fromResource(markerResourceId));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bikeSharingStationCoordinates, 13));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
