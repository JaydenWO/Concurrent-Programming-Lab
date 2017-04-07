package com.example.acer.jinwifidirect.repository;

/**
 * Created by Acer on 27/3/2017.
 */
import java.net.Socket;

public class SocketUtil
{
    private static SocketUtil socketutil = null;
    private Socket socket;
    private int connected;
    public void SocketsUtil() {};

    public static SocketUtil getInstance()
    {
        if(socketutil == null)
        {
            socketutil = new SocketUtil();
        }
        return socketutil;
    }

    public void initialize(Socket s,int c)
    {
        socket = s;
        connected = c;
    }

    public void storeSocket(Socket s)
    {
        socket = s;
    }

    public Socket getSocket()
    {
        return socket ;
    }

    public void setConnect()
    {
        connected =1;
    }

    public void setUnconnect()
    {
        connected = 0;
    }

    public int getConnect()
    {
        return connected;
    }
}
