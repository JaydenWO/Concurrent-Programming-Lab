package com.example.pc.androidpractice;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Acer on 27/3/2017.
 */
public class ClientReceiveMessageThread implements Runnable
{
    private WiFiMainActivity activity;
    private String hostAddress;
    private boolean isConnected = true;
    public ClientReceiveMessageThread(WiFiMainActivity acti, String host)
    {
        activity = acti;
        hostAddress = host;
    }

    @Override
    public void run()
    {
        while(isConnected) {
            int timeout = 10000;
            int port = 8888;
            Socket socket = null;

            InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);

            try {
                socket = new Socket();
                socket.bind(null);
                socket.connect(socketAddress, timeout);

                // TODO Start Receiving Messages
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final String message = br.readLine();
                br.close();
                //

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setTextDisplay(message);
                    }
                });

            } catch (IOException e) {
                Log.v("error", e.toString());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
            }
        }
    }

    //get set
    public void setIsConnected(Boolean b)
    {
        isConnected = b;
    }
}
