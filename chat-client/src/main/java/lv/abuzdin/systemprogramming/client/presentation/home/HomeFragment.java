package lv.abuzdin.systemprogramming.client.presentation.home;

import android.os.Bundle;
import lv.abuzdin.systemprogramming.client.R;
import lv.abuzdin.systemprogramming.client.infrastructure.common.DateService;
import lv.abuzdin.systemprogramming.client.presentation.BaseFragment;

import javax.inject.Inject;


public class HomeFragment extends BaseFragment {

    @Inject
    DateService dateService;

    @Override
    protected int contentViewId() {
        return R.layout.main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }
}
