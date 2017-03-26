package com.example.acer.jinwifidirect;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Acer on 22/3/2017.
 */
public class ClientSendMessageThread implements Runnable
{
    private String message;
    private int port, timeout;
    private String hostAddress;
    private ClientActivity activity;

    public ClientSendMessageThread(String m,String host, int time, ClientActivity acti) {
        message = m;
        hostAddress = host;
        timeout = time;
        activity = acti;
    }

    @Override
    public void run()
    {
        int port = 8888;
        Socket socket = null;
        InetSocketAddress socketAddress = new InetSocketAddress(hostAddress, port);

        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect(socketAddress, timeout);

            //send message
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            pw.println(message);
            pw.close();
            //

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
