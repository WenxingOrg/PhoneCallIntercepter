package wzhao1.phoneintercepter.fragment;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import wzhao1.phoneintercepter.R;
import wzhao1.phoneintercepter.service.BindReceiverService;
import wzhao1.phoneintercepter.util.PreferenceUtil;

/**
 * Created by wzhao1 on 16/5/18.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = "SettingFragment";

    public static List<String> INTERCEPTER_1 = new ArrayList<>();
    public static List<String> INTERCEPTER_2 = new ArrayList<>();

    public static final String CARD_KEY_1 = "card_1";
    public static final String CARD_KEY_2 = "card_2";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] stringArray = getResources().getStringArray(R.array.card_selection);

        SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        SubscriptionInfo card1Info = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0);
        INTERCEPTER_1.clear();
        for (String selection : stringArray) {
            INTERCEPTER_1.add(String.format(selection, card1Info.getCarrierName()));
        }
        ListPreference intercepter_1 = (ListPreference) findPreference("intercepter_type_1");
        intercepter_1.setTitle(card1Info.getCarrierName());
        String[] intercepter1 = new String[2];
        intercepter_1.setEntries(INTERCEPTER_1.toArray(intercepter1));
        intercepter_1.setEntryValues(INTERCEPTER_1.toArray(intercepter1));
        String card1 = PreferenceUtil.getSharedPreference(getActivity(), CARD_KEY_1);
        for (int i = 0; i < INTERCEPTER_1.size(); i++) {
            if (card1.equals(INTERCEPTER_1.get(i))) {
                intercepter_1.setValueIndex(i);
                break;
            } else {
                intercepter_1.setValueIndex(1);
            }
        }


        intercepter_1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                PreferenceUtil.setSharedPreference(getActivity(), CARD_KEY_1, value);
                Log.i(TAG, "card 1 intercepter : " + value);
                return true;
            }
        });

        ListPreference intercepter_2 = (ListPreference) findPreference("intercepter_type_2");
        SubscriptionInfo card2Info = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1);
        if (card2Info == null) {//single card just show one option.
            ((PreferenceCategory)findPreference("Intercepter")).removePreference(intercepter_2);
            return;
        }
        INTERCEPTER_2.clear();
        for (String selection : stringArray) {
            INTERCEPTER_2.add(String.format(selection, card2Info.getCarrierName()));
        }
        intercepter_2.setTitle(card2Info.getCarrierName());

        String[] intercepter2 = new String[2];
        intercepter_2.setEntries(INTERCEPTER_2.toArray(intercepter2));
        intercepter_2.setEntryValues(INTERCEPTER_2.toArray(intercepter2));
        String card2 = PreferenceUtil.getSharedPreference(getActivity(), CARD_KEY_2);
        for (int i = 0; i < INTERCEPTER_2.size(); i++) {
            if (card2.equals(INTERCEPTER_2.get(i))) {
                intercepter_2.setValueIndex(i);
                break;
            } else {
                intercepter_2.setValueIndex(1);

            }
        }

        intercepter_2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                PreferenceUtil.setSharedPreference(getActivity(), CARD_KEY_2, value);
                Log.i(TAG, "card 2 intercepter : " + value);
                return true;
            }
        });

        //start service once start the app.
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("wzhao1.phoneintercepter.service.BindReceiverService".equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        if (!isServiceRunning) {
            Log.i(TAG, "Restart the service");
            Intent i = new Intent(getActivity(), BindReceiverService.class);
            getActivity().startService(i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), BindReceiverService.class));

    }

    //    private Object subManagerMethod(String method, Object... vars) {
//        Object subManagerMethod = null;
//        try {
//            SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
//
//            Class classSub = Class.forName(subscriptionManager.getClass().getName());
//            Method methodSub;
//            if (vars == null) {
//                methodSub = classSub.getDeclaredMethod(method);
//                methodSub.setAccessible(true);
//                subManagerMethod = methodSub.invoke(subscriptionManager);
//            }else {
//                Class[] className = new Class[vars.length];
//                for (int i = 0; i < vars.length; i++) {
//                    className[i] = vars[i].getClass();
//                }
//                methodSub = classSub.getDeclaredMethod(method, vars.getClass());
//                methodSub.setAccessible(true);
//                subManagerMethod = methodSub.invoke(subscriptionManager, className);
//            }
//        } catch (Exception ex) { // Many things can go wrong with reflection calls
//            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
//            return subManagerMethod;
//        }
//        return subManagerMethod;
//    }
}
