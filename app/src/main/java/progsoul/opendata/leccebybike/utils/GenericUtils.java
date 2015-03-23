package progsoul.opendata.leccebybike.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ProgSoul on 10/03/2015.
 */
public class GenericUtils {
    public static List<Pair<String, Integer>> getColors(Context context) {
        List<Pair<String, Integer>> colors = new ArrayList<>();
        try {
            Field[] fields = Class.forName(context.getPackageName() + ".R$color").getDeclaredFields();
            for (Field field : fields) {
                String colorName = field.getName();
                int colorId = field.getInt(null);
                int color = context.getResources().getColor(colorId);
                colors.add(new Pair(colorName, color));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return colors;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

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


}
