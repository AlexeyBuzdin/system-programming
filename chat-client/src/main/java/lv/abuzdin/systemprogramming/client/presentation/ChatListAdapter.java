package lv.abuzdin.systemprogramming.client.presentation;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import lv.abuzdin.systemprogramming.client.R;
import lv.abuzdin.systemprogramming.client.presentation.common.BaseListAdapter;

import javax.inject.Inject;

public class ChatListAdapter extends BaseListAdapter<Message>{

    @Inject
    LayoutInflater inflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.chat_item, null);
        TextView text = ButterKnife.findById(view, R.id.messageText);
        Message message = get(position);
        text.setText(message.getValue());
        Resources resources = getContext().getResources();
        if(message.isClient()) {
            view.setBackgroundColor(resources.getColor(R.color.client_user));
        } else {
            view.setBackgroundColor(resources.getColor(R.color.other_user));
        }

        return view;
    }
}
