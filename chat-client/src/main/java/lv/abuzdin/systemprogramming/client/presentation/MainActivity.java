package lv.abuzdin.systemprogramming.client.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Bus;
import lv.abuzdin.systemprogramming.client.BaseApplication;
import lv.abuzdin.systemprogramming.client.R;

import javax.inject.Inject;
import java.io.DataInputStream;
import java.net.Socket;

public class MainActivity extends SherlockFragmentActivity {

    public static final String KEY_IP = "KEY_IP";
    public static final String KEY_PORT = "KEY_PORT";
    @Inject
    Bus bus;

    @Inject
    Context context;

    @Inject
    LayoutInflater inflater;

    @Inject
    ChatListAdapter adapter;



    @InjectView(R.id.chatView)
    ListView chatView;

    @InjectView(R.id.messageEditText)
    EditText messageEditText;

    private DataInputStream inputStream;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.home);

        // Android constructs Activity instances so we must find the ObjectGraph instance and inject this.
        BaseApplication.inject(this);
        ButterKnife.inject(this);

        startSocket();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_connect:
                openConnect();
                return true;
            case R.id.action_refresh:
                startSocket();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSocket() {
       try {
           SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
           String ip = prefs.getString(KEY_IP, "");
           String port = prefs.getString(KEY_PORT, "");

           final Socket client = new Socket(ip, Integer.parseInt(port));

           new Thread(() -> {
               try (DataInputStream inputStream = new DataInputStream(client.getInputStream())){
                   this.inputStream = inputStream;

                   while (true) {
                       addMessage(this.inputStream.readUTF());
                       Thread.sleep(1000);
                   }
               } catch (Exception ignored) {}
           }).start();
       } catch (Exception e) {
           Toast.makeText(this, "Failed to start Server Socket", Toast.LENGTH_SHORT).show();
       }
    }

    private void addMessage(String s) {
        adapter.getData().add(s);
        adapter.notifyDataSetChanged();
    }

    private void openConnect() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        View view = inflater.inflate(R.layout.dialog, null);
        final EditText ipEditText = ButterKnife.findById(view, R.id.ip_edit_text);
        ipEditText.setText(prefs.getString(KEY_IP, ""));

        final EditText portEditText = ButterKnife.findById(view, R.id.port_edit_text);
        portEditText.setText(prefs.getString(KEY_PORT, ""));

        new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo))
                .setTitle("Connect to server")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor edit = prefs.edit();

                        String ip = ipEditText.getText().toString();
                        edit.putString(KEY_IP, ip);

                        String port = portEditText.getText().toString();
                        edit.putString(KEY_PORT, port);
                        edit.commit();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setView(view)
                .show();

    }
}
