package progsoul.opendata.leccebybike.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.activities.CyclePathInfoActivity;
import progsoul.opendata.leccebybike.activities.MainActivity;
import progsoul.opendata.leccebybike.entities.CyclePath;
import progsoul.opendata.leccebybike.interfaces.AsyncRetrieveCyclePathsTaskResponse;
import progsoul.opendata.leccebybike.libs.residemenu.ResideMenu;
import progsoul.opendata.leccebybike.tasks.RetrieveCyclePathsTask;
import progsoul.opendata.leccebybike.utils.Constants;
import progsoul.opendata.leccebybike.utils.CustomSharedPreferences;
import progsoul.opendata.leccebybike.utils.GenericUtils;

/**
 * Created by ProgSoul on 27/03/2015.
 */
public class CyclePathsMapFragment extends Fragment implements OnMapReadyCallback, AsyncRetrieveCyclePathsTaskResponse {
    private ArrayList<CyclePath> cyclePaths;
    private Map<Marker, CyclePath> markersHashMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_map, container, false);

        ResideMenu resideMenu = ((MainActivity) getActivity()).getResideMenu();

        // add gesture operation's ignored views
        FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);

        cyclePaths = CustomSharedPreferences.getSavedCyclePaths(getActivity());
        if (cyclePaths == null)
            new RetrieveCyclePathsTask(true, getActivity(), this).execute();
        else {
            // get map with an async operation
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        return parentView;
    }

    @Override
    public void onAsyncTaskCompleted(ArrayList<CyclePath> cyclePaths) {
        if (cyclePaths != null) {
            this.cyclePaths = cyclePaths;
            CustomSharedPreferences.saveCyclePaths(getActivity(), cyclePaths);
            // get map with an async operation
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }  else {
            Toast.makeText(getActivity(), "Impossibile recuperare le piste ciclabili. Riprovare più tardi.", Toast.LENGTH_LONG).show();
            ((MainActivity) getActivity()).changeFragment(new BikeSharingStationsMapFragment());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng lecceCoordinates = new LatLng(40.35152, 18.17502);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lecceCoordinates, 10));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        String[] colors = getResources().getStringArray(R.array.colors_palette);
        for(CyclePath cyclePath : cyclePaths) {
            PolylineOptions polylineOptions = new PolylineOptions();
            double[] latitudes = cyclePath.getLatitudes();
            double[] longitudes = cyclePath.getLongitudes();
            for (int i = 0; i < latitudes.length; i++)
                polylineOptions.add(new LatLng(latitudes[i], longitudes[i]));
            Pair<Integer, Float> polylineColor = GenericUtils.getColorBasedOnCyclePathType(cyclePath, colors);
            polylineOptions.color(polylineColor.first);

            MarkerOptions markerOptions = new MarkerOptions()
                    .title(cyclePath.getName())
                    .position(new LatLng(latitudes[0], longitudes[0]))
                    .icon(BitmapDescriptorFactory.defaultMarker(polylineColor.second));
            markersHashMap.put(googleMap.addMarker(markerOptions), cyclePath);
            googleMap.addPolyline(polylineOptions);
        }
        googleMap.setInfoWindowAdapter(infoWindowAdapter);
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    private GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(getActivity(), CyclePathInfoActivity.class);
            CyclePath cyclePath = markersHashMap.get(marker);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.CYCLE_PATH, cyclePath);
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
            View customInfoWindow = getActivity().getLayoutInflater().inflate(R.layout.cycle_path_info_window, null);
            CyclePath cyclePath = markersHashMap.get(marker);

            TextView cyclePathNameTextView = (TextView) customInfoWindow.findViewById(R.id.cyclePathName);
            cyclePathNameTextView.setText(GenericUtils.ellipsize(cyclePath.getName(), 30));
            TextView cyclePathTypeTextView = (TextView) customInfoWindow.findViewById(R.id.cyclePathType);
            cyclePathTypeTextView.setText(cyclePathTypeTextView.getText().toString() + cyclePath.getFeatures().getType().toString().toLowerCase());
            TextView cyclePathDistanceTextView = (TextView) customInfoWindow.findViewById(R.id.cyclePathDistance);
            cyclePathDistanceTextView.setText(cyclePathDistanceTextView.getText().toString() + cyclePath.getFeatures().getDistance());

            return customInfoWindow;
        }
    };
}