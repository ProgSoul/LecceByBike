package progsoul.opendata.leccebybike.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import progsoul.opendata.leccebybike.entities.CyclePath;
import progsoul.opendata.leccebybike.interfaces.AsyncRetrieveCyclePathsTaskResponse;
import progsoul.opendata.leccebybike.views.RosetteProgressDialog;

/**
 * Created by ProgSoul on 26/03/2015.
 */
public class RetrieveCyclePathsTask extends AsyncTask<Void, Integer, Integer> {
    private RosetteProgressDialog rosetteProgressDialog;
    private AsyncRetrieveCyclePathsTaskResponse delegate;
    private ArrayList<CyclePath> cyclePaths;
    private Context context;
    private boolean isProgressDialogVisible;

    public RetrieveCyclePathsTask(Context context, AsyncRetrieveCyclePathsTaskResponse delegate) {
        this(false, context, delegate);
    }

    public RetrieveCyclePathsTask(boolean isProgressDialogVisible, Context context, AsyncRetrieveCyclePathsTaskResponse delegate) {
        this.isProgressDialogVisible = isProgressDialogVisible;
        this.delegate = delegate;
        this.cyclePaths = new ArrayList<>();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (isProgressDialogVisible) {
            rosetteProgressDialog = new RosetteProgressDialog(context);
            rosetteProgressDialog.show();
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String cyclePathsInfosURL = "http://www.bloodynose.it/notar/cyclepaths/retrievecyclepathsinfos.php";
            HttpGet httpGet = new HttpGet(cyclePathsInfosURL);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity);

            JSONArray jsonArray = new JSONArray(response);
            int jsonArrayLength = jsonArray.length();
            for (int i = 0; i < jsonArrayLength; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                cyclePaths.add(convertJSONObjectToCyclePath(jsonObject));
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

    private CyclePath convertJSONObjectToCyclePath(JSONObject obj) throws JSONException{
        String name = obj.getString("name");
        String description = obj.getString("description");

        JSONArray latitudeJSONArray = obj.getJSONArray("Latitude");
        int latitudeJSONArrayLength = latitudeJSONArray.length();
        double[] latitudes = new double[latitudeJSONArrayLength];
        for (int i = 0; i < latitudeJSONArrayLength; i++)
            latitudes[i] = latitudeJSONArray.getDouble(i);

        JSONArray longitudeJSONArray = obj.getJSONArray("Longitude");
        int longitudeJSONArrayLength = longitudeJSONArray.length();
        double[] longitudes = new double[longitudeJSONArrayLength];
        for (int i = 0; i < longitudeJSONArrayLength; i++)
            longitudes[i] = longitudeJSONArray.getDouble(i);

        CyclePath.Features features = convertJSONObjectToCyclePathFeatures(obj.getJSONObject("features"));
        CyclePath.Details details = convertJSONObjectToCyclePathDetails(obj.getJSONObject("details"));

        return new CyclePath(name, description, latitudes, longitudes, features, details);
    }

    private CyclePath.Features convertJSONObjectToCyclePathFeatures(JSONObject obj) throws JSONException {
        String distance = obj.getString("distance");
        boolean isSuitableForChildren = Boolean.valueOf(obj.getString("is_suitable_for_children"));
        boolean isSuitableForSkater = Boolean.valueOf(obj.getString("is_suitable_for_skaters"));
        CyclePath.TYPE type = CyclePath.TYPE.valueOf(obj.getString("type").toUpperCase());
        CyclePath.ROAD_SURFACE roadSurface = CyclePath.ROAD_SURFACE.valueOf(obj.getString("road_surface").toUpperCase());

        return new CyclePath.Features(distance, isSuitableForChildren, isSuitableForSkater, type, roadSurface);
    }

    private CyclePath.Details convertJSONObjectToCyclePathDetails(JSONObject obj) throws JSONException{
        String averageSlope = obj.getString("average_slope");
        String maxSlope = obj.getString("max_slope");
        String trackDensity = obj.getString("track_density");
        String difference = obj.getString("difference");
        String ascentDifference = obj.getString("ascent_difference");
        String descentDifference = obj.getString("descent_difference");

        return new CyclePath.Details(averageSlope, maxSlope, trackDensity, difference, ascentDifference, descentDifference);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d("Cycle paths result", integer.toString());

        if (rosetteProgressDialog != null && rosetteProgressDialog.isShowing()) {
            rosetteProgressDialog.dismiss();
            rosetteProgressDialog = null;
        }

        if (cyclePaths.isEmpty()) {
            delegate.onAsyncTaskCompleted(null);
        } else {
            delegate.onAsyncTaskCompleted(cyclePaths);
        }
    }
}