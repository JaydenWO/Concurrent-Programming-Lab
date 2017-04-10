package com.example.acer.jinwifidirect;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.acer.jinwifidirect.repository.IdUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClientActivity extends AppCompatActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private HashMap<String, String> buddies = new HashMap<String, String>();
    //An intent filter declares  what an activity or service can do and what types of broadcasts a receiver can handle.
    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private ArrayAdapter adapter;
    private String a [] = new String [100];
    private String hostAddress;
    private int number;
    private ListView listView;
    //private ClientThread clientThread;

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
        deletePersistentGroups();
        adapter = new ArrayAdapter<WifiP2pDevice>(this,android.R.layout.simple_list_item_1, deviceList);
        ListView listView = (ListView)findViewById(R.id.listView2);
        listView.setAdapter(adapter);

        //listView to show the peers list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = deviceList.get(position).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 0 ;

                //connecting
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        //clear the list
                        deviceList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(ClientActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onStart()
    {
        mManager.removeGroup(mChannel,null);
        super.onStart();
        deviceList.clear();
        mManager.clearServiceRequests(mChannel,null);
        deletePersistentGroups();
    }

    private void discoverService() {
        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName,
                                                String registrationType, WifiP2pDevice srcDevice) {

                // A service has been discovered. Is this our app?
                if (instanceName.equalsIgnoreCase("_test")) {

                    // Update the device name with the human-friendly version from
                    // the DnsTxtRecord, assuming one arrived
                    srcDevice.deviceName = buddies
                            .containsKey(srcDevice.deviceAddress) ? buddies
                            .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                    // update the UI and add the item the discovered
                    // device.
                    deviceList.add(srcDevice);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            /**
             * A new TXT record is available. Pick up the advertised
             * buddy name.
             */
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomainName, Map<String, String> record, WifiP2pDevice device) {

                Log.d("client", "DnsSdTxtRecord available -" + record.toString());
                buddies.put(device.deviceAddress, record.get("buddyname"));

            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int arg0) {
            }
        });

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(ClientActivity.this, "Searching for Room", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Toast.makeText(ClientActivity.this, "Fail to connect Room", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(ClientActivity.this, "Failed adding service discovery request", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void searchPeer(View v)
    {
        deviceList.clear();
        adapter.notifyDataSetChanged();
        discoverService();
    }

    public void setHostAddress(String address)
    {
        hostAddress = address;
    }

    public void setDiscoverButton (boolean b) {
        Button btn = (Button)findViewById(R.id.searchBtn);
        btn.setEnabled(b);
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


    public void clientBack(View v)
    {
        Intent i = new Intent(ClientActivity.this, MenuSelection.class);
        startActivity(i);
    }

    public void toClientChat()
    {
        Intent i = new Intent(ClientActivity.this,ClientChatActivity.class);
        startActivity(i);
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
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
