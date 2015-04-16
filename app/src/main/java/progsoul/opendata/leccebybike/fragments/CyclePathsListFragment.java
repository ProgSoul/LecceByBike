package progsoul.opendata.leccebybike.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.activities.CyclePathInfoActivity;
import progsoul.opendata.leccebybike.entities.CyclePath;
import progsoul.opendata.leccebybike.interfaces.AsyncRetrieveCyclePathsTaskResponse;
import progsoul.opendata.leccebybike.libs.custompulltorefresh.PullToRefreshView;
import progsoul.opendata.leccebybike.libs.parallaxrecyclerview.ParallaxRecyclerAdapter;
import progsoul.opendata.leccebybike.listeners.RecyclerItemClickListener;
import progsoul.opendata.leccebybike.tasks.RetrieveCyclePathsTask;
import progsoul.opendata.leccebybike.utils.Constants;
import progsoul.opendata.leccebybike.utils.CustomSharedPreferences;
import progsoul.opendata.leccebybike.utils.GPSTracker;

/**
 * Created by ProgSoul on 01/04/2015.
 */
public class CyclePathsListFragment extends Fragment implements AsyncRetrieveCyclePathsTaskResponse {
    private ParallaxRecyclerAdapter<CyclePath> adapter;
    private PullToRefreshView pullToRefreshView;
    public ArrayList<CyclePath> cyclePaths;
    private RecyclerView cyclePathsRecyclerListView;
    private GPSTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view_list, container, false);
        cyclePaths = CustomSharedPreferences.getSavedCyclePaths(getActivity());
        gpsTracker = new GPSTracker(getActivity());
        // check if GPS enabled
        if(gpsTracker.canGetLocation()){
            Location myLocation = new Location("");
            myLocation.setLatitude(gpsTracker.getLatitude());
            myLocation.setLongitude(gpsTracker.getLongitude());
            sortByDistanceFromMyLocation(myLocation);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }

        cyclePathsRecyclerListView = (RecyclerView) rootView.findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cyclePathsRecyclerListView.setLayoutManager(layoutManager);
        //cyclePathsRecyclerListView.setHasFixedSize(true);

        adapter = new ParallaxRecyclerAdapter<>(cyclePaths);
        adapter.implementRecyclerAdapterMethods(new CyclePathsAdapterMethod());
        final View parallaxHeader = getActivity().getLayoutInflater().inflate(R.layout.cycle_path_header_row, cyclePathsRecyclerListView, false);
        adapter.setParallaxHeader(parallaxHeader, cyclePathsRecyclerListView);

        cyclePathsRecyclerListView.setAdapter(adapter);
        cyclePathsRecyclerListView.addOnItemTouchListener(recyclerItemClickListener);

        pullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh_view);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RetrieveCyclePathsTask(false, getActivity(), CyclePathsListFragment.this).execute();
            }
        });

        return rootView;
    }

    private void sortByDistanceFromMyLocation(Location myLocation) {
        for (CyclePath cyclePath : cyclePaths) {
            Location cyclePathLocation = new Location(cyclePath.getName());
            cyclePathLocation.setLatitude(cyclePath.getLatitudes()[0]);
            cyclePathLocation.setLongitude(cyclePath.getLongitudes()[0]);
            float distance = myLocation.distanceTo(cyclePathLocation);
            cyclePath.setDistanceFromMyLocation(distance);
        }

        Collections.sort(cyclePaths, new Comparator<CyclePath>() {
            @Override
            public int compare(CyclePath lhs, CyclePath rhs) {
                Float lhsDistance = lhs.getDistanceFromMyLocation();
                Float rhsDistance = rhs.getDistanceFromMyLocation();
                return lhsDistance.compareTo(rhsDistance);
            }
        });
    }

    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getActivity(), cyclePathsRecyclerListView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (position > 0) {
                Intent intent = new Intent(getActivity(), CyclePathInfoActivity.class);
                CyclePath cyclePath = cyclePaths.get(position - 1);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.CYCLE_PATH, cyclePath);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {
            // do nothing
        }
    });

    @Override
    public void onAsyncTaskCompleted(ArrayList<CyclePath> cyclePaths) {
        if (cyclePaths != null && !cyclePaths.equals(CustomSharedPreferences.getSavedBikeSharingStations(getActivity()))) {
            adapter.setData(cyclePaths);
            CustomSharedPreferences.saveCyclePaths(getActivity(), cyclePaths);
        }

        pullToRefreshView.setRefreshing(false);
    }

    public class CyclePathsAdapterMethod implements ParallaxRecyclerAdapter.RecyclerAdapterMethods {
        private LayoutInflater layoutInflater;
        private String[] colors;

        CyclePathsAdapterMethod() {
            layoutInflater = getActivity().getLayoutInflater();
            colors = getResources().getStringArray(R.array.colors_palette);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            CyclePath cyclePath = cyclePaths.get(i);
            CyclePathViewHolder cyclePathViewHolder = (CyclePathViewHolder) viewHolder;

            cyclePathViewHolder.itemRowContainer.setBackgroundColor(Color.parseColor(colors[i % colors.length]));

            float distanceFromMyLocation = cyclePath.getDistanceFromMyLocation();
            if (distanceFromMyLocation < 1000)
                cyclePathViewHolder.cyclePathDistanceTextView.setText(distanceFromMyLocation + " m");
            else {
                distanceFromMyLocation = distanceFromMyLocation / 1000f;
                cyclePathViewHolder.cyclePathDistanceTextView.setText(String.format("%.2f", distanceFromMyLocation) + " Km");
            }

            cyclePathViewHolder.cyclePathNameTextView.setText(cyclePath.getName());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            final View inflatedRow = layoutInflater.inflate(R.layout.cycle_path_item_row, viewGroup, false);
            return new CyclePathViewHolder(inflatedRow);
        }

        @Override
        public int getItemCount() {
            return cyclePaths.size();
        }
    }

    public final class CyclePathViewHolder extends RecyclerView.ViewHolder{
        View itemRowContainer;
        TextView cyclePathDistanceTextView;
        TextView cyclePathNameTextView;

        public CyclePathViewHolder(View itemView) {
            super(itemView);
            itemRowContainer = itemView.findViewById(R.id.cycle_path_item_row_container);
            cyclePathDistanceTextView = (TextView) itemView.findViewById(R.id.cycle_path_distance_label);
            cyclePathNameTextView = (TextView) itemView.findViewById(R.id.cycle_path_name_label);
        }
    }
}
