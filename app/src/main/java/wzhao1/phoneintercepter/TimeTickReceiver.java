package wzhao1.phoneintercepter;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import wzhao1.phoneintercepter.service.BindReceiverService;

/**
 * Created by wzhao1 on 16/6/20.
 */
public class TimeTickReceiver extends BroadcastReceiver {
    private final static String TAG = "TimeTickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(TAG, "TimeTickReceiver receive======>");
        boolean isServiceRunning = false;

        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            //检查Service状态
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                Log.e(TAG, service.service.getClassName());
                if ("wzhao1.phoneintercepter.service.BindReceiverService".equals(service.service.getClassName())) {
                    isServiceRunning = true;
                }
            }
            if (!isServiceRunning) {
                Log.i(TAG, "Restart the service");
                Intent i = new Intent(context, BindReceiverService.class);
                context.startService(i);
            }
        }
    }
}
