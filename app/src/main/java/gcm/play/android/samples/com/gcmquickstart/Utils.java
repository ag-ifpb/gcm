package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dk on 7/13/2015.
 */
public class Utils {

    public Context mContext;
    public SharedPreferences mSharePreferences;

    public Utils(Context context){
        this.mContext = context;
    }

    public String getPref(String prefName){
        mSharePreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mSharePreferences.getString(prefName,"none");
    }

}
