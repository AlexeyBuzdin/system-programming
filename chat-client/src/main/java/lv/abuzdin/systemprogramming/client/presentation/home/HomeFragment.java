package lv.abuzdin.systemprogramming.client.presentation.home;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.InjectView;
import lv.abuzdin.systemprogramming.client.R;
import lv.abuzdin.systemprogramming.client.infrastructure.common.DateService;
import lv.abuzdin.systemprogramming.client.presentation.BaseFragment;

import javax.inject.Inject;
import java.util.List;


public class HomeFragment extends BaseFragment {

    @Inject
    DateService dateService;

    @Inject
    ChatListAdapter adapter;

    @InjectView(R.id.chatView)
    ListView chatView;
    @InjectView(R.id.messageEditText)
    EditText messageEditText;

    @Override
    protected int contentViewId() {
        return R.layout.home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        List<String> data = adapter.getData();
        data.add("Hello");
        data.add("Hello");
        data.add("Hello");
        data.add("Hello");
        data.add("Hello");
        data.add("Hello");
        data.add("Hello");
        adapter.setData(data);
        chatView.setAdapter(adapter);
//        Socket client = new Socket(HOST, PORT);
    }
}
