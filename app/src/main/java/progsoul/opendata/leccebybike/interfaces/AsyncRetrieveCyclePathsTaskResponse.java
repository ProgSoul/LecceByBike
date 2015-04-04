package progsoul.opendata.leccebybike.interfaces;

import java.util.ArrayList;

import progsoul.opendata.leccebybike.entities.CyclePath;

/**
 * Created by ProgSoul on 26/03/2015.
 */
public interface AsyncRetrieveCyclePathsTaskResponse {
    /**
     * Pass the response object to this method in order to manage it in an activity
     */
    void onAsyncTaskCompleted(ArrayList<CyclePath> cyclePaths);
}
