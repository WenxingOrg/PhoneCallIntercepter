package wzhao1.phoneintercepter.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import wzhao1.phoneintercepter.receiver.CallReceiver;
import wzhao1.phoneintercepter.receiver.TimeTickReceiver;

/**
 * Created by wzhao1 on 16/6/20.
 */
public class BindReceiverService extends Service {

    IntentFilter filter, filter1;
    TimeTickReceiver receiver;
    CallReceiver receiver1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("BindReceiverService", "oncreate()");
        filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiver = new TimeTickReceiver();
        registerReceiver(receiver, filter);

        filter1 = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter1.setPriority(2000);
        receiver1 = new CallReceiver();
        registerReceiver(receiver1, filter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
        }
        if (receiver1 != null) {
            unregisterReceiver(receiver1);
        }
    }

}
