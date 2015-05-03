package progsoul.opendata.leccebybike.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.activities.BikeSharingStationInfoActivity;
import progsoul.opendata.leccebybike.activities.MainActivity;
import progsoul.opendata.leccebybike.entities.BikeSharingStation;
import progsoul.opendata.leccebybike.libs.residemenu.ResideMenu;
import progsoul.opendata.leccebybike.libs.segmentedgroup.SegmentedGroup;
import progsoul.opendata.leccebybike.utils.CustomSharedPreferences;
import progsoul.opendata.leccebybike.utils.Constants;

/**
 * Created by ProgSoul on 19/03/2015.
 */
public class BikeSharingStationsMapFragment extends Fragment implements OnMapReadyCallback{
    private ArrayList<BikeSharingStation> bikeSharingStations;
    private Map<Marker, BikeSharingStation> markersHashMap = new HashMap<>();
    private int lastCheckedId = -1;
    private SegmentedGroup bikeStationTypeRadioGroup;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_bike_sharing_stations_map, container, false);
        bikeSharingStations = CustomSharedPreferences.getSavedBikeSharingStations(getActivity());

        ResideMenu resideMenu = ((MainActivity) getActivity()).getResideMenu();

        // add gesture operation's ignored views
        FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);

        bikeStationTypeRadioGroup = (SegmentedGroup) parentView.findViewById(R.id.typeSegmentedGroup);
        bikeStationTypeRadioGroup.setVisibility(View.GONE);
        bikeStationTypeRadioGroup.setOnCheckedChangeListener(radioGroupListener);

        // get map with an async operation
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return parentView;
    }

    private RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            googleMap.clear();
            if (lastCheckedId == checkedId) {
                lastCheckedId = -1;
                populateMapWithAllBikeSharingStations();
            } else {
                lastCheckedId = checkedId;
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isBikeSharingStationEnabled = checkedRadioButton.getText().toString().equals("attive");
                filterBikeSharingStationByEnabled(isBikeSharingStationEnabled);
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng lecceCoordinates = new LatLng(40.35152, 18.17502);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lecceCoordinates, 13));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);

        // turns radio group visible only if map has been loaded
        bikeStationTypeRadioGroup.setVisibility(View.VISIBLE);
        populateMapWithAllBikeSharingStations();

        googleMap.setInfoWindowAdapter(infoWindowAdapter);
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    private void populateMapWithAllBikeSharingStations() {
        // if already populated, then clear all markers
        if (!markersHashMap.isEmpty())
            markersHashMap.clear();

        for(BikeSharingStation bikeSharingStation : bikeSharingStations) {
            //if bike station is operative, then color marker will be green, else red
            int markerResourceId = bikeSharingStation.isOperative() ? R.drawable.marker_attiva : R.drawable.marker_nonattiva;
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(bikeSharingStation.getName())
                    .position(new LatLng(bikeSharingStation.getLatitude(), bikeSharingStation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(markerResourceId));
            markersHashMap.put(googleMap.addMarker(markerOptions), bikeSharingStation);
        }
    }

    private void filterBikeSharingStationByEnabled(boolean isBikeSharingStationEnabled) {
        //clear hashmap from all markers already added
        markersHashMap.clear();
        //if bike station is operative, then color marker will be green, else red
        int markerResourceId = isBikeSharingStationEnabled ? R.drawable.marker_attiva : R.drawable.marker_nonattiva;
        for(BikeSharingStation bikeSharingStation : bikeSharingStations) {
            //if bike station is operative, then color marker will be green, else red
            if (isBikeSharingStationEnabled == bikeSharingStation.isOperative()) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(bikeSharingStation.getName())
                        .position(new LatLng(bikeSharingStation.getLatitude(), bikeSharingStation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(markerResourceId));
                markersHashMap.put(googleMap.addMarker(markerOptions), bikeSharingStation);
            }
        }
    }

    private GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(getActivity(), BikeSharingStationInfoActivity.class);
            BikeSharingStation bikeSharingStation = markersHashMap.get(marker);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BIKE_SHARING_STATION, bikeSharingStation);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }
    };

    private GoogleMap.InfoWindowAdapter infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View customInfoWindow = getActivity().getLayoutInflater().inflate(R.layout.bike_sharing_station_info_window, null);
            BikeSharingStation bikeSharingStation = markersHashMap.get(marker);

            TextView bikeSharingStationNameTextView = (TextView) customInfoWindow.findViewById(R.id.bikeSharingStationName);
            bikeSharingStationNameTextView.setText(bikeSharingStation.getName());
            TextView bikeSharingStationAvailablePlacesTextView = (TextView) customInfoWindow.findViewById(R.id.bikeSharingStationAvailablePlaces);
            bikeSharingStationAvailablePlacesTextView.setText(bikeSharingStationAvailablePlacesTextView.getText().toString() + bikeSharingStation.getAvailablePlaces());
            TextView bikeSharingStationFreeBikesTextView = (TextView) customInfoWindow.findViewById(R.id.bikeSharingStationFreeBikes);
            bikeSharingStationFreeBikesTextView.setText(bikeSharingStationFreeBikesTextView.getText().toString() + bikeSharingStation.getFreeBikes());

            return customInfoWindow;
        }
    };
}