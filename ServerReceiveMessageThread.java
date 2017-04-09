package com.example.acer.jinwifidirect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Acer on 22/3/2017.
 */
public class ServerReceiveMessageThread implements Runnable
{
    ServerSocket serverSocket = null;
    private boolean isConnected = true;
    private HostChatActivity activity;

    public ServerReceiveMessageThread(HostChatActivity acti)
    {
        activity = acti;
    }


    @Override
    public void run()
    {
        try {
            //keep waiting for new client to connect
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(1111));

            while(true) {
                try {
                    final Socket serverClient = serverSocket.accept();

                    BufferedReader br = new BufferedReader(new InputStreamReader(serverClient.getInputStream()));
                    final String message = br.readLine();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.updateReceiveChat(message);
                        }
                    });
                } catch (IOException e) {
                    Log.v("error", e.toString());
                    break;
                }
            }

        } catch (IOException e) {
            Log.v("error", e.toString());
        } finally {
            if(serverSocket != null  && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void interruptSocket()
    {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIsConnected(Boolean b)
    {
        isConnected = b;
    }
}
