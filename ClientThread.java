package com.example.acer.jinwifidirect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Acer on 7/4/2017.
 */
public class ClientThread implements Runnable
{
    private ClientActivity activity;
    private String hostAddress;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public enum Result {
        JOIN_ROOM,LEAVE_ROOM,;
    }
    public ClientThread (ClientActivity acti,String host)
    {
        activity = acti;
        hostAddress = host;
    }
    @Override
    public void run()
    {
        int timeout = 10000;
        int port = 9999;
        Socket socket = null;
        InetSocketAddress socketAddress
                = new InetSocketAddress(hostAddress, port);
        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect(socketAddress, timeout);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(ServerThread.Request.JOIN_ROOM);
            oos.flush();
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                final Result requestCode = (Result) ois.readObject();
                switch (requestCode) {
                    case JOIN_ROOM:
                        joinRoom();
                        break;
                    case LEAVE_ROOM:
                        leaveRoom();
                        break;
                }
            }catch (ClassNotFoundException e) {
                Log.v("error", e.toString());
            }

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

    private void joinRoom() throws IOException {
        activity.toClientChat();
    }

    private void leaveRoom() throws IOException {
        activity.reSearch();
    }
}