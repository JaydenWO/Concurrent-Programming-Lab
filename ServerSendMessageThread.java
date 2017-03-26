package com.example.acer.jinwifidirect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Acer on 22/3/2017.
 */
public class ServerSendMessageThread implements Runnable
{
    private ServerSocket serverSocket;
    private String message;
    private HostActivity activity;

    public ServerSendMessageThread(String m, HostActivity acti)
    {
        message = m;
        activity = acti;
    }

    public void run() {
        try {
            /**
             * Listing 16-25: Creating a Server Socket
             */
            serverSocket = new ServerSocket(8888);
            //send message
            while(true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
                pw.println(message);
                pw.close();
            }
        } catch (IOException e) {
            Log.v("error", e.toString());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
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
