package lv.abuzdin.systemprogramming.client.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import lv.abuzdin.systemprogramming.client.R;
import lv.abuzdin.systemprogramming.client.presentation.common.BaseListAdapter;

import javax.inject.Inject;

public class ChatListAdapter extends BaseListAdapter<String>{

    @Inject
    LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.chat_item, null);
        TextView text = ButterKnife.findById(view, R.id.messageText);
        text.setText(get(position));

        return view;
    }
}
