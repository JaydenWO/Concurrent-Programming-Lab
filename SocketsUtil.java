package com.example.pc.androidpractice;

/**
 * Created by Acer on 27/3/2017.
 */
import java.net.Socket;
public class SocketsUtil
{
    private Socket[] socketutil = new Socket[1];
    public void SocketsUtil(Socket[]s)
    {
        socketutil = s;
    }

    public void storeSocket(Socket s)
    {
        socketutil[0]=s;
    }

    public Socket getSocket()
    {
        return socketutil[0];
    }
}
