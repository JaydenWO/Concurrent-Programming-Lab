package com.example.pc.androidpractice;

/**
 * Created by Acer on 27/3/2017.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private WiFiMainActivity activity;
    private List<WifiP2pDevice> deviceList;


    public WiFiDirectBroadcastReceiver() {
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager m, Channel c, WiFiMainActivity acti, List<WifiP2pDevice> list) {
        super();
        manager = m;
        channel = c;
        activity = acti;
        deviceList = list; //situational
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) { //check if wifi direct is on
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setDiscoverButtonEnable(true);
            } else {
                activity.setDiscoverButtonEnable(false);
                //activity.resetData();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) { //check if list changed or got peers

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            /** this is where you display all ur list of available peers **/
            if (manager != null) {
                manager.requestPeers(channel, new PeerListListener() {
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        //execute here
                        deviceList.clear();
                        deviceList.addAll(peers.getDeviceList());
                        activity.updateList();
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) { //if no manager thn do nth
                return;
            }

            //extract network info
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            //check if we are connected
            if (networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel,
                        new ConnectionInfoListener() {
                            public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                                // If the connection is established
                                if (info.groupFormed && info.isGroupOwner) {
                                } else if (info.groupFormed) {
                                    activity.setHostAddress(info.groupOwnerAddress.getHostAddress());
                                    activity.clientStartReceivingMessage();
                                    activity.disableSendButton(false);

                                } else {
                                    // It's a disconnect
                                    activity.disableSendButton(true);
                                    deviceList.clear();
                                    activity.updateList();
                                    activity.clientStopReceivingMessage();
                                    activity.interruptSocket();

                                }
                            }
                        });
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


            }
        }
    }
}