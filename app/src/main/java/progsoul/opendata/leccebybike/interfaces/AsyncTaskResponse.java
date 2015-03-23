package progsoul.opendata.leccebybike.interfaces;

import java.util.ArrayList;

import progsoul.opendata.leccebybike.entities.BikeSharingStation;

/**
 * This interface handles the callback from AsyncTasks in order to manage the response
 * @author ProgSoul
 *
 */
public interface AsyncTaskResponse {
    /**
     * Pass the response object to this method in order to manage it in an activity
     */
    void onAsyncTaskCompleted(ArrayList<BikeSharingStation> bikeSharingStations);
}