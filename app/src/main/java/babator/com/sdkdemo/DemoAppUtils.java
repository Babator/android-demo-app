package babator.com.sdkdemo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

class DemoAppUtils {

    public static String[][] loadArray(Context context, int arrId){
        String[][] array;
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
