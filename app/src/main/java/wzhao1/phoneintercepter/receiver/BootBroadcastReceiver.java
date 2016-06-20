package wzhao1.phoneintercepter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import wzhao1.phoneintercepter.service.BindReceiverService;

/**
 * Created by wzhao1 on 16/6/20.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context, BindReceiverService.class);
        context.startService(service);
        Log.v("TAG", "开机自动服务自动启动.....");
        //启动应用，参数为需要自动启动的应用的包名
        Intent intent1 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(intent1);
    }

}
