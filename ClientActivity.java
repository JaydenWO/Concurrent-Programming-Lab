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

public class ClientActivity extends AppCompatActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    //An intent filter declares  what an activity or service can do and what types of broadcasts a receiver can handle.
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ClientReceiveMessageThread clientReceiveThread;
    private ClientSendMessageThread clientSendThread;
    private String message = new String();
    private String hostAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        ListView listView = (ListView)findViewById(R.id.listView2);
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
                        Toast.makeText(ClientActivity.this, "Fail to connect. Retry.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setHostAddress(String address)
    {
        hostAddress = address;
    }

    public void clientStartReceivingMessage()
    {
        if(clientReceiveThread == null) {
            clientReceiveThread = new ClientReceiveMessageThread(hostAddress,this);
            Thread t = new Thread(clientReceiveThread);
            t.start();
        }
    }

    public void clientStopReceivingMessage()
    {
        if(clientReceiveThread != null)
        {
            clientReceiveThread.setIsConnected(false);
            clientReceiveThread = null;
        }
    }

    public void clientSendButton(View v)
    {
        EditText messageET = (EditText)findViewById(R.id.editText2);
        String message = messageET.getText().toString();
        setClientMessage(message);
        ClientSendMessageThread sendThread = new ClientSendMessageThread(message,hostAddress, 10000,this);
        Thread t = new Thread(sendThread);
        t.start();
    }

    public void searchPeer (View v)
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(ClientActivity.this, "Fail to discover : " + reasonCode,
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
                Toast.makeText(ClientActivity.this, "Fail to disconnect : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void setClientMessage(String s)
    {
        TextView messageTV = (TextView)findViewById(R.id.clientTextView);
        messageTV.setText(s);
    }

    public void setDiscoverButton (boolean b) {
        Button btn = (Button)findViewById(R.id.searchBtn);
        btn.setEnabled(b);
    }

    public void updateList()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new ClientBroadcastReceiver(mManager, mChannel, this, deviceList);
        registerReceiver(mReceiver, intentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}
