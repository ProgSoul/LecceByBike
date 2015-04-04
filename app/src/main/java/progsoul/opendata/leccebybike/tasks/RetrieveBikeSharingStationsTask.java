package progsoul.opendata.leccebybike.tasks;

import android.os.AsyncTask;
import android.util.Log;

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

import progsoul.opendata.leccebybike.entities.BikeSharingStation;
import progsoul.opendata.leccebybike.interfaces.AsyncRetrieveBikeSharingStationsTaskResponse;
import progsoul.opendata.leccebybike.utils.GenericUtils;

/**
 * Created by ProgSoul on 08/03/2015.
 */
public class RetrieveBikeSharingStationsTask extends AsyncTask<Void, Integer, Integer> {
    private NumberProgressBar progressBar;
    private AsyncRetrieveBikeSharingStationsTaskResponse delegate;
    private ArrayList<BikeSharingStation> bikeSharingStations;

    public RetrieveBikeSharingStationsTask(AsyncRetrieveBikeSharingStationsTaskResponse delegate) {
        this(null, delegate);
    }

    public RetrieveBikeSharingStationsTask(NumberProgressBar progressBar, AsyncRetrieveBikeSharingStationsTaskResponse delegate) {
        this.progressBar = progressBar;
        this.delegate = delegate;
        this.bikeSharingStations = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String bikeSharingInfosURL = "http://bloodynose.it/notar/bikesharingstations/retrievebikesharinginfos.php";
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
        String imageURL = GenericUtils.getStreetViewImageURL(latitude, longitude);

        return new BikeSharingStation(name, isOperative, freeBikes, address, availablePlaces, latitude, longitude, imageURL);
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
