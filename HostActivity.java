package com.example.acer.jinwifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HostActivity extends AppCompatActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    //An intent filter declares  what an activity or service can do and what types of broadcasts a receiver can handle.
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ServerReceiveMessageThread serverReceiveMessageThread;
    private ServerSendMessageThread serverSendMessageThread;
    private String message = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        //Get an instance of the WifiP2pManager, and call its initialize() method.
        // This method returns a WifiP2pManager.Channel object, which you'll use later to connect your app to the Wi-Fi P2P framework
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);
        //listView to show the peers list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = deviceList.get(position).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 15;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        //receiver will handle
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(HostActivity.this, "Fail to connect. Retry.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void updateList()
    {
        adapter.notifyDataSetChanged();
    }
    /* register the broadcast receiver with the intent values to be matched */
    //Register the intent filter and broadcast receiver when your main activity is active, and unregister them when the activity is paused.
    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new HostBroadcastReceiver(mManager, mChannel, this, deviceList);
        registerReceiver(mReceiver, intentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
    public void setDiscoverButton (boolean b) {
        Button btn = (Button)findViewById(R.id.discoverBtn);
        btn.setEnabled(b);
    }

    public void serverSendingMessage()
    {
        //sendThread = new ServerSendMessageThread(8867, this, messageQueue);
        serverSendMessageThread = new ServerSendMessageThread(message,this);
        Thread t = new Thread(serverReceiveMessageThread);
        t.start();
    }

    public void serverReceivingMessage()
    {
        serverReceiveMessageThread = new ServerReceiveMessageThread(this);
        Thread t = new Thread(serverSendMessageThread);
        t.start();
    }

    public void setServerMessage(String s)
    {
        TextView messageTV = (TextView)findViewById(R.id.textView2);
        messageTV.setText(s);
    }

    public void interruptSocket()
    {
        if(serverSendMessageThread != null) {
            serverSendMessageThread.interruptSocket();
        }
        if(serverSendMessageThread != null)
        {
            serverSendMessageThread.interruptSocket();
        }
    }

    public void serverSendButton(View v)
    {
        EditText messageET = (EditText)findViewById(R.id.editText);
        //String message = messageET.getText().toString();
        setServerMessage(messageET.getText().toString());
    }

    public void searchPeer(View v)
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(HostActivity.this, "Fail to discover : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disconnectChat (View v)
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(HostActivity.this, "Fail to disconnect : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }

        });
    }
}
