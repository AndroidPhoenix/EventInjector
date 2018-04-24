package phoenix.com.eventinjector.util;

import android.util.Log;
import android.widget.Toast;


import com.phoenix.eventinjector.Events;

import java.util.Random;

import phoenix.com.eventinjector.XApplication;

/**
 * Created by zhenghui on 2018/4/13.
 */

public class InjectUtil {
    private static final String TAG = "InjectUtil";

    private static Events mEvent = new Events();

    private static int mSelectedDev = -1;

    public static void init() {
        int res = mEvent.Init();
        Log.d(TAG, "init: res " + res);
    }

    public static void injectEventNode() {
        int index = 0;
        for(Events.InputDevice dev : mEvent.m_Devs) {
            if(dev == null){
                continue;
            }
            Log.d(TAG, "injectEventNode: " + dev.getPath());
            if(dev.getPath() != null && dev.getPath().contains("event4")) {
                if(dev.Open(true)) {
                    mSelectedDev = index;
                    Toast.makeText(XApplication.getContext(), "Device opened successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(XApplication.getContext(), "Device failed to open. Do you have root?", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            index++;
        }
    }

    public static void releaseDev() {
        if(mSelectedDev < 0) {
            return;
        }
        mEvent.m_Devs.get(mSelectedDev).Close();
    }

    public static void sendMoveEvent() {
        try {
            mEvent.m_Devs.get(mSelectedDev).SendMoveAbs(50, 0, 50, 600);
        } catch (Exception e) {
            Log.d(TAG, "sendRandom: " + e.getMessage());
        }
    }
}
