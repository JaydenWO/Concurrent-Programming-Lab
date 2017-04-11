package com.example.acer.jinwifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.acer.jinwifidirect.repository.SocketUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostActivity extends AppCompatActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    //An intent filter declares  what an activity or service can do and what types of broadcasts a receiver can handle.
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private WifiP2pDnsSdServiceInfo service;
    //private ServerThread serverThread;
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
        if (mManager != null && mChannel != null) {
            deletePersistentGroups();
        }
        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);

    }

    private void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("available", "visible");
        record.put("buddyname", "Annoymous Room." + (int) (Math.random() * 1000));
        service = WifiP2pDnsSdServiceInfo.newInstance(
                "_test", "_presence._tcp", record);

        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(HostActivity.this, "Cleared All Local Service", Toast.LENGTH_SHORT).show();
                mManager.addLocalService(mChannel, service, null);
                mManager.discoverPeers(mChannel, null);
            }

            @Override
            public void onFailure(int code) {

            }

        });
    }

    public void hostBack(View v)
    {
        Intent i = new Intent(HostActivity.this, MenuSelection.class);
        startActivity(i);
        mManager.removeGroup(mChannel,null);
    }

    public void searchPeer(View v)
    {
        startRegistration();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mManager.removeGroup(mChannel,null);
        mManager.clearLocalServices(mChannel, null);
        mManager.stopPeerDiscovery(mChannel, null);
        deletePersistentGroups();
        SocketUtil.getInstance().setEmptySocket();

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
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /*public void hostReplyMessage()
    {
        serverThread = new ServerThread(this);
        Thread t = new Thread(serverThread);
        t.start();
    }*/

    public void toHostChat()
    {
        Intent i = new Intent(HostActivity.this, HostChatActivity.class);
        startActivity(i);
        mManager.removeLocalService(mChannel, service,null);

    }

    public void disconnectChat (View v)
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {

            }

        });
    }

    public void setDiscoverButton (boolean b) {
        Button btn = (Button)findViewById(R.id.discoverBtn);
        btn.setEnabled(b);
    }

    private void deletePersistentGroups() {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(mManager, mChannel, netid, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
