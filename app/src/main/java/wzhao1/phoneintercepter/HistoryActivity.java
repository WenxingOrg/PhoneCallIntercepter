package wzhao1.phoneintercepter;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wzhao1.phoneintercepter.util.PreferenceUtil;

public class HistoryActivity extends Activity {
    ListView phoneList;
    List<String> phoneListString = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_history);
        phoneList = (ListView) findViewById(R.id.phoneList);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(View.VISIBLE);
        textView.setTextSize(18);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        textView.setPadding(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics), 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics));
        textView.setText("拦截记录");
        phoneList.addHeaderView(textView);
        String phoneJson = PreferenceUtil.getSharedPreference(this, "phone_json");
        try {
            phoneListString.clear();
            JSONObject json = new JSONObject(phoneJson);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                phoneListString.add(keys.next());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(phoneListString.size() == 0) {
            return;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, phoneListString);
        phoneList.setAdapter(adapter);
    }

}
