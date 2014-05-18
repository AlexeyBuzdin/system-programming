package lv.abuzdin.systemprogramming.client.presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.Bus;
import lv.abuzdin.systemprogramming.client.BaseApplication;
import lv.abuzdin.systemprogramming.client.R;
import lv.abuzdin.systemprogramming.client.presentation.home.HomeFragment;

import javax.inject.Inject;

public class MainActivity extends SherlockFragmentActivity {

    @Inject
    Bus bus;

    @Inject
    Context context;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);

        // Android constructs Activity instances so we must find the ObjectGraph instance and inject this.
        BaseApplication.inject(this);

        HomeFragment homeFragment = new HomeFragment();
        BaseApplication.inject(homeFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, homeFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public void changeFragment(Fragment fragment) {
        changeFragment(fragment, null);
    }

    public void changeFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(null)
                .commit();
    }
}
