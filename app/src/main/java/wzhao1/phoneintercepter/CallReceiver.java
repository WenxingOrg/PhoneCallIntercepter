package wzhao1.phoneintercepter;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import wzhao1.phoneintercepter.fragment.SettingsFragment;
import wzhao1.phoneintercepter.util.PreferenceUtil;

/**
 * Created by wzhao1 on 16/5/18.
 */
public class CallReceiver extends BroadcastReceiver {

    private Context context;
    private static final String TAG = "tag";
    private TelephonyManager telMgr;
    private static Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int subs = intent.getIntExtra("subscription", -1);
        this.context = context;
        synchronized (intent) {
            if(subs != Integer.MAX_VALUE || this.intent == null) {
                this.intent = intent;
            }
        }
        telMgr.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
//        switch (callState) {
//            case TelephonyManager.CALL_STATE_RINGING:
//                cardFilter(intent);
//                break;
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                break;
//            case TelephonyManager.CALL_STATE_IDLE:
//                break;
//        }
        String resultData = getResultData();
        Log.v(TAG, "resultData: " + resultData);
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    cardFilter(intent);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void cardFilter(Intent intent) {
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        int subs = intent.getIntExtra("subscription", -1);
        Log.v(TAG, "number:" + number);

        boolean b = needEndCall(subs, number);
        if (b) {
            String phoneJson = PreferenceUtil.getSharedPreference(context, "phone_json");
            JSONObject jsonObject = null;
            number = number + "      " + new SimpleDateFormat().format(System.currentTimeMillis());
            try {
                if(phoneJson.isEmpty()) {
                    jsonObject = new JSONObject();
                } else {
                    jsonObject = new JSONObject(phoneJson);
                }
                jsonObject.put(number, number);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PreferenceUtil.setSharedPreference(context, "phone_json", jsonObject.toString());
            endCall();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private boolean needEndCall(int subId, String number) {
        String card1 = PreferenceUtil.getSharedPreference(context, SettingsFragment.CARD_KEY_1);
        String card2 = PreferenceUtil.getSharedPreference(context, SettingsFragment.CARD_KEY_2);
        SubscriptionManager sub = SubscriptionManager.from(context);
        SubscriptionInfo activeSubscriptionInfo = sub.getActiveSubscriptionInfo(subId);

        boolean needEndCall = false;

        if(activeSubscriptionInfo == null || number == null) {
            return needEndCall;
        }

        switch (activeSubscriptionInfo.getSimSlotIndex()) {
            case 0://card 1
                if (card1.equals(SettingsFragment.INTERCEPTER_1.get(0))) {
                    if (!getPhoneNum(context).contains(number)) {
                        needEndCall = true;
                    }
                }
                break;
            case 1://card 2
                if (card2.equals(SettingsFragment.INTERCEPTER_2.get(0))) {
                    if (!getPhoneNum(context).contains(number)) {
                        needEndCall = true;
                    }
                }
                break;
            default:
                Log.e("zwx", "no this call");
                break;
        }
        return needEndCall;
    }

    public Object telephonyMethod(String methodName, Object var) {
        Object telephonyInterface = null;
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony;
            if (var == null) {
                methodGetITelephony = classTelephony.getDeclaredMethod(methodName);
                methodGetITelephony.setAccessible(true);
                telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            }else {
                methodGetITelephony = classTelephony.getDeclaredMethod(methodName, var.getClass());
                methodGetITelephony.setAccessible(true);
                telephonyInterface = methodGetITelephony.invoke(telephonyManager, var);
            }
        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return telephonyInterface;
        }
        return telephonyInterface;
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            boolean isEnd = (boolean) telephonyMethod("endCall", null);
            Log.e(TAG, "End call1." + isEnd);
            if(!isEnd) {
                Toast.makeText(context, "Sorry it is not working for your phone.", Toast.LENGTH_LONG).show();
                telephonyMethod("silenceRinger", null);
            }
            //            iTelephony.endCall();
        } catch (Exception e) {
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }

    private ArrayList<String> getPhoneNum(Context context) {
        ArrayList<String> numList = new ArrayList<String>();
        //得到ContentResolver对象
        ContentResolver cr = context.getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            // 取得联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            // 取得电话号码(可能存在多个号码)
            while (phone.moveToNext()) {
                String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                numList.add(strPhoneNumber);
                Log.v("tag", "strPhoneNumber:" + strPhoneNumber);
            }

            phone.close();
        }
        cursor.close();
        return numList;
    }
}
