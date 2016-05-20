package wzhao1.phoneintercepter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


import wzhao1.phoneintercepter.fragment.SettingsFragment;

/**
 * Created by wzhao1 on 16/5/18.
 */
public class MainActivity extends FragmentActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsFragment()).commit();
    }

    public void interceptPhone(View view) {
        startActivity(new Intent(this, HistoryActivity.class));
    }
}
