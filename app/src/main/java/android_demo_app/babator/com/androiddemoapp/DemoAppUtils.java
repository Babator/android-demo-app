package android_demo_app.babator.com.androiddemoapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;


/**
 * Created by danshneider on 05/09/2016.
 */
public class DemoAppUtils {

    public static String[][] loadArray(Context context, int arrId){
        String[][] array = null;
        try {
            Resources res = context.getResources();
            TypedArray ta = res.obtainTypedArray(arrId);
            int n = ta.length();
            array = new String[n][];
            for (int i = 0; i < n; ++i) {
                int id = ta.getResourceId(i, 0);
                if (id > 0) {
                    array[i] = res.getStringArray(id);
                } else {
                    array = null;
                }
            }
            ta.recycle();
        }
        catch (Exception e){
            array = null;
        }

        return array;
    }
}