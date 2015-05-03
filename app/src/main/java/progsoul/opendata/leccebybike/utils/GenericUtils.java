package progsoul.opendata.leccebybike.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import progsoul.opendata.leccebybike.R;
import progsoul.opendata.leccebybike.entities.CyclePath;

/**
 * Created by ProgSoul on 10/03/2015.
 */
public class GenericUtils {
    public static String ellipsize(String input, int maxLength) {
        String ellip = "...";
        if (input == null || input.length() <= maxLength
                || input.length() < ellip.length()) {
            return input;
        }
        return input.substring(0, maxLength - ellip.length()).concat(ellip);
    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static String getStreetViewImageURL(double latitude, double longitude) {
        String googleStreetViewApiKey = "AIzaSyDoE0akrBvl1f3IIRLpuXpVBDsxTfa4ceg";

        StringBuilder streetViewImageURL = new StringBuilder();
        streetViewImageURL.append("http://maps.googleapis.com/maps/api/streetview?size=600x400&location=");
        streetViewImageURL.append(latitude + "," + longitude);
        streetViewImageURL.append("&sensor=false&key=" + googleStreetViewApiKey);
        streetViewImageURL.append("&heading=250&fov=90&pitch=-10");

        return streetViewImageURL.toString();
    }

    public static Pair<Integer, Integer> getColorBasedOnCyclePathType(CyclePath.TYPE type, String[] colors) {
        switch (type) {
            case STRADA:
                return new Pair<>(Color.parseColor(colors[8]), R.drawable.marker_strada);
            case CICLOSTRADA:
                return new Pair<>(Color.parseColor(colors[2]), R.drawable.marker_ciclostrada);
            case SENTIERO:
                return new Pair<>(Color.parseColor(colors[4]), R.drawable.marker_sentiero);
            case CICLABILE:
                return new Pair<>(Color.parseColor(colors[5]), R.drawable.marker_ciclabile);
            case CICLOPEDONALE:
                return new Pair<>(Color.parseColor(colors[7]), R.drawable.marker_ciclopedonale);
            default:
                return null;
        }
    }
}
