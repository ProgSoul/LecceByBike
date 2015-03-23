package progsoul.opendata.leccebybike.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.activities.BikeSharingStationInfoActivity;
import progsoul.opendata.leccebybike.entities.BikeSharingStation;
import progsoul.opendata.leccebybike.interfaces.AsyncTaskResponse;
import progsoul.opendata.leccebybike.libs.custompulltorefresh.PullToRefreshView;
import progsoul.opendata.leccebybike.listeners.RecyclerItemClickListener;
import progsoul.opendata.leccebybike.tasks.RetrieveBikeSharingStationsTask;
import progsoul.opendata.leccebybike.utils.BikeSharingStationsSharedPreferences;
import progsoul.opendata.leccebybike.utils.Constants;

/**
 * Created by ProgSoul on 19/03/2015.
 */
public class BikeSharingStationsListFragment extends Fragment implements AsyncTaskResponse{
    private BikeSharingListRecyclerViewAdapter adapter;
    private PullToRefreshView pullToRefreshView;
    private ArrayList<BikeSharingStation> bikeSharingStations;
    private RecyclerView bikeSharingStationsRecyclerListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bike_sharing_list, container, false);

        bikeSharingStationsRecyclerListView = (RecyclerView) rootView.findViewById(R.id.list_view);
        bikeSharingStations = BikeSharingStationsSharedPreferences.getSavedBikeSharingStations(getActivity());
        adapter = new BikeSharingListRecyclerViewAdapter(bikeSharingStations);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bikeSharingStationsRecyclerListView.setLayoutManager(layoutManager);
        bikeSharingStationsRecyclerListView.setAdapter(adapter);
        bikeSharingStationsRecyclerListView.addOnItemTouchListener(recyclerItemClickListener);

        pullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh_view);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RetrieveBikeSharingStationsTask(BikeSharingStationsListFragment.this).execute();
            }
        });
        return rootView;
    }

    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getActivity(), bikeSharingStationsRecyclerListView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Intent intent = new Intent(getActivity(), BikeSharingStationInfoActivity.class);
            BikeSharingStation bikeSharingStation = bikeSharingStations.get(position);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BIKE_SHARING_STATION, bikeSharingStation);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            // do nothing
        }
    });

    @Override
    public void onAsyncTaskCompleted(ArrayList<BikeSharingStation> bikeSharingStations) {
        if (bikeSharingStations != null && !bikeSharingStations.isEmpty() && !bikeSharingStations.equals(BikeSharingStationsSharedPreferences.getSavedBikeSharingStations(getActivity()))) {
            adapter.setBikeSharingStations(bikeSharingStations);
            BikeSharingStationsSharedPreferences.saveBikeSharingStations(getActivity(), bikeSharingStations);
        }

        pullToRefreshView.setRefreshing(false);
    }

    public class BikeSharingListRecyclerViewAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
        private ArrayList<BikeSharingStation> bikeSharingStations;
        private String[] colors;
        private LayoutInflater layoutInflater;
        private Integer[] iconDrawableResources = {
                R.drawable.stazione_foroboario,
                R.drawable.stazione_parchexcarlopranzo,
                R.drawable.stazione_parchexarenaaurora,
                R.drawable.stazione_sanoronzo,
                R.drawable.stazione_piazzamazzini,
                R.drawable.stazione_palazzoalleanza,
                R.drawable.stazione_stazioneffss,
                R.drawable.stazione_viariosto,
                R.drawable.stazione_setteleacquarie,
                R.drawable.stazione_sanbiagio,
                R.drawable.stazione_torredelparco
        };

        BikeSharingListRecyclerViewAdapter(ArrayList<BikeSharingStation> bikeSharingStations) {
            if (bikeSharingStations == null)
                throw new IllegalArgumentException("items must not be null");

            this.colors = getResources().getStringArray(R.array.colors_palette);
            this.bikeSharingStations = bikeSharingStations;
            this.layoutInflater = getActivity().getLayoutInflater();
        }

        public void setBikeSharingStations(ArrayList<BikeSharingStation> bikeSharingStations) {
            this.bikeSharingStations = bikeSharingStations;
            notifyDataSetChanged();
        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_row, viewGroup, false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
            BikeSharingStation bikeSharingStation = bikeSharingStations.get(position);

            viewHolder.itemNameTextView.setText(bikeSharingStation.getName());
            viewHolder.itemIconImageView.setImageResource(iconDrawableResources[position]);
            viewHolder.itemLayout.setBackgroundColor(Color.parseColor(colors[position % colors.length]));
        }

        @Override
        public int getItemCount() {
            return bikeSharingStations.size();
        }
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        TextView itemNameTextView;
        ImageView itemIconImageView;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.item_linear_layout);
            itemNameTextView = (TextView) itemView.findViewById(R.id.item_name_label);
            itemIconImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }
    }
}
