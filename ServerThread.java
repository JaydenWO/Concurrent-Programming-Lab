package com.example.acer.jinwifidirect;

/**
 * Created by Acer on 7/4/2017.
 */
import android.util.Log;

import com.example.acer.jinwifidirect.repository.SocketUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable
{
    public enum Request {
        JOIN_ROOM;
    }

    private HostActivity activity;
    private ServerSocket serverSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public ServerThread (HostActivity acti)
    {
        activity = acti;
    }
    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(9999);
            Socket serverClient = serverSocket.accept();
            ois = new ObjectInputStream(serverClient.getInputStream());
            try {
                final Request requestCode = (Request) ois.readObject();
                switch (requestCode) {
                    case JOIN_ROOM:
                        joinRoom(serverClient);
                        break;
                }
            }
            catch (ClassNotFoundException e) {
                Log.v("error", e.toString());
            }
        }
        catch (IOException e)
        {
            Log.v("error", e.toString());
        }finally {
            if(serverSocket != null  && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void joinRoom(Socket s) throws IOException {
        if(SocketUtil.getInstance().getConnect()==0)
        {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(ClientThread.Result.JOIN_ROOM);
            oos.flush();
            activity.toHostChat();

        }

        else
        {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(ClientThread.Result.LEAVE_ROOM);
            oos.flush();
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
