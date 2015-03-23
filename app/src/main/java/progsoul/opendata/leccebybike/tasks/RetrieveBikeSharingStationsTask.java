package progsoul.opendata.leccebybike.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

import progsoul.opendata.leccebybike.activities.MainActivity;
import progsoul.opendata.leccebybike.entities.BikeSharingStation;
import progsoul.opendata.leccebybike.interfaces.AsyncTaskResponse;
import progsoul.opendata.leccebybike.utils.Constants;

/**
 * Created by ProgSoul on 08/03/2015.
 */
public class RetrieveBikeSharingStationsTask extends AsyncTask<Void, Integer, Integer> {
    private NumberProgressBar progressBar;
    private String googleStreetViewApiKey = "AIzaSyDoE0akrBvl1f3IIRLpuXpVBDsxTfa4ceg";
    private final String bikeSharingInfosURL = "http://www.bloodynose.it/openlecce/retrievebikesharinginfos.php";
    private AsyncTaskResponse delegate;
    private ArrayList<BikeSharingStation> bikeSharingStations;

    public RetrieveBikeSharingStationsTask(AsyncTaskResponse delegate) {
        this(null, delegate);
    }

    public RetrieveBikeSharingStationsTask(NumberProgressBar progressBar, AsyncTaskResponse delegate) {
        this.progressBar = progressBar;
        this.delegate = delegate;
        this.bikeSharingStations = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(bikeSharingInfosURL);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity);

            JSONArray jsonArray = new JSONArray(response);
            int jsonArrayLength = jsonArray.length();
            for (int i = 0; i < jsonArrayLength; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                bikeSharingStations.add(convertJSONObjectToBikeSharingStation(jsonObject));
                // Update the progress bar after every step
                int progress = (int) ((i / (float) jsonArrayLength) * 100);
                publishProgress(progress);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
            return 0;
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
            return 0;
        }

        return 1;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressBar != null)
            progressBar.setProgress(values[0]);
    }

    private BikeSharingStation convertJSONObjectToBikeSharingStation(JSONObject obj) throws JSONException{
        String name = obj.getString("name");
        boolean isOperative = Boolean.valueOf(obj.getString("is_operative"));
        int freeBikes = Integer.valueOf(obj.getString("free_bikes"));
        int availablePlaces = Integer.valueOf(obj.getString("available_places"));
        String address = obj.getString("address");
        double latitude = Double.valueOf(obj.getString("latitude"));
        double longitude =  Double.valueOf(obj.getString("longitude"));
        String imageURL = getStreetViewImageURL(latitude, longitude);

        return new BikeSharingStation(name, isOperative, freeBikes, address, availablePlaces, latitude, longitude, imageURL);
    }

    private String getStreetViewImageURL(double latitude, double longitude) {
        StringBuilder streetViewImageURL = new StringBuilder();
        streetViewImageURL.append("http://maps.googleapis.com/maps/api/streetview?size=600x400&location=");
        streetViewImageURL.append(latitude + "," + longitude);
        streetViewImageURL.append("&sensor=false&key=" + googleStreetViewApiKey);
        streetViewImageURL.append("&heading=250&fov=90&pitch=-10");

        return streetViewImageURL.toString();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d("Bike stations result", integer.toString());

        if (bikeSharingStations.isEmpty()) {
            delegate.onAsyncTaskCompleted(null);
        } else {
            delegate.onAsyncTaskCompleted(bikeSharingStations);
        }
    }
}
