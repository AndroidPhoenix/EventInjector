package phoenix.com.eventinjector;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by zhenghui on 2018/4/9.
 */

public class XApplication extends Application {
    private static final String TAG = "XApplication";

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mContext = this.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
