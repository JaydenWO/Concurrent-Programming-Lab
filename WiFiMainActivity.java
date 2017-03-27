package com.example.pc.androidpractice;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WiFiMainActivity extends AppCompatActivity {

    private WifiP2pManager manager;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private String hostAddress;
    private ClientReceiveMessageThread receiveThread;
    private ServerSendMessageThread sendThread;
    private String message;
    private SocketsUtil socketsUtil;


    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_main);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = deviceList.get(position).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 15;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        //receiver will handle
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(WiFiMainActivity.this, "Connect failed. Retry.",
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

    //discover peer button
    public void discoverPeerButton(View v)
    {
        //start to discover peers
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiMainActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WiFiMainActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setHostAddress(String address)
    {
        hostAddress = address;
    }

    public void clientStopReceivingMessage()
    {
        if(receiveThread != null)
        {
            receiveThread.setIsConnected(false);
            receiveThread = null;
        }
    }

    public void interruptSocket()
    {
        if(sendThread != null)
        {
            sendThread.interruptSocket();
        }
    }
    //disconnect peer button
    public void disconnectPeerButton(View v)
    {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WiFiMainActivity.this, "Failure to disconnect : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void setDiscoverButtonEnable(boolean b)
    {
        Button btn = (Button)findViewById(R.id.discoverBtn);
        btn.setEnabled(b);
    }

    public void setTextDisplay(String s)
    {
        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText(s);
    }

    public void serverStartSending()
    {
        EditText messageET = (EditText)findViewById(R.id.editText);
        String message = messageET.getText().toString();
        sendThread = new ServerSendMessageThread(this,message,socketsUtil);
        Thread t = new Thread(sendThread);
        t.start();
    }
    public void serverSendMessageButton(View v)
    {

    }

    public void clientStartReceivingMessage()
    {
        receiveThread = new ClientReceiveMessageThread(this,hostAddress);
        Thread t = new Thread(receiveThread);
        t.start();
    }

    public void disableSendButton(boolean a)
    {
        Button btn = (Button)findViewById(R.id.sendBtn);
        btn.setEnabled(a);
    }

    @Override
    public void onResume() {
        super.onResume();
        //set the broadcast receiver
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, deviceList);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
