package com.example.pc.androidpractice;

import android.util.Log;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Acer on 27/3/2017.
 */
public class ServerSendMessageThread implements Runnable
{
    private ServerSocket serverSocket;
    private WiFiMainActivity activity;
    private String message;
    private SocketsUtil socketsUtil;
    public ServerSendMessageThread (WiFiMainActivity acti, String m,SocketsUtil s)
    {
        activity = acti;
        message = m;
        socketsUtil = s;
    }

    @Override
    public void run()
    {
        try {
            serverSocket = new ServerSocket(8888);

            while(true) {
                try {

                    Socket serverClient = serverSocket.accept();
                    // TODO Start Sending Messages
                    socketsUtil.storeSocket(serverClient);

                } catch (IOException e) {
                    break;
                }
            }

        } catch (IOException e) {
            Log.v("error", e.toString());
        } finally {
            if(serverSocket != null && !serverSocket.isClosed()) {
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
}
