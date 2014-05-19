package lv.abuzdin.systemprogramming.client.presentation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class MainActivity extends SherlockFragmentActivity {

    public static final String KEY_IP = "KEY_IP";
    public static final String KEY_PORT = "KEY_PORT";
    public static final String KEY_USERNAME = "KEY_USERNAME";

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

    private Socket client;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.home);

        // Android constructs Activity instances so we must find the ObjectGraph instance and inject this.
        BaseApplication.inject(this);
        ButterKnife.inject(this);

        initViews();

        startSocket();
    }

    private void initViews() {
        chatView.setAdapter(adapter);

        messageEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        EditText edit = (EditText) v;
                        String text = edit.getText().toString();
                        outputStream.writeUTF(text);
                        addMessage("Me: " + text, true);
                        edit.getText().clear();
                    } catch (IOException e) {
                        Log.e("TAG", "Exception", e);
                    }
                }
                return false;
            }
        });
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
        stopSocket();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        String ip = prefs.getString(KEY_IP, "");
                        String port = prefs.getString(KEY_PORT, "");
                        String username = prefs.getString(KEY_USERNAME, "Anonymous");

                        client = new Socket(ip, Integer.parseInt(port));
                        inputStream = new DataInputStream(client.getInputStream());
                        outputStream = new DataOutputStream(client.getOutputStream());
                        outputStream.writeUTF(username);

                        while (true) {
                           addMessage(inputStream.readUTF(), false);
                           Thread.sleep(1000);
                        }
                    } catch (SocketException ignore) {
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
           }).start();
       } catch (Exception e) {
           Toast.makeText(this, "Failed to establish connection", Toast.LENGTH_SHORT).show();
           Log.e("TAG", "Exception", e);
       }
    }

    private void stopSocket() {
        try {
            if (client != null) client.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            Log.e("TAG", "Exception", e);
        }
    }

    private void addMessage(final String s, final boolean client) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.getData().add(new Message(client, s));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openConnect() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        View view = inflater.inflate(R.layout.dialog, null);
        final EditText usernameEditText = ButterKnife.findById(view, R.id.username_edit_text);
        usernameEditText.setText(prefs.getString(KEY_USERNAME, ""));

        final EditText ipEditText = ButterKnife.findById(view, R.id.ip_edit_text);
        ipEditText.setText(prefs.getString(KEY_IP, ""));

        final EditText portEditText = ButterKnife.findById(view, R.id.port_edit_text);
        portEditText.setText(prefs.getString(KEY_PORT, ""));

        new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo))
                .setTitle("Connect to server")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor edit = prefs.edit();

                        String username = usernameEditText.getText().toString();
                        edit.putString(KEY_USERNAME, username);

                        String ip = ipEditText.getText().toString();
                        edit.putString(KEY_IP, ip);

                        String port = portEditText.getText().toString();
                        edit.putString(KEY_PORT, port);

                        edit.commit();
                        startSocket();
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
