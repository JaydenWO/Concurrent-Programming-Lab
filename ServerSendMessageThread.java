package com.example.acer.jinwifidirect;

import android.util.Log;

import com.example.acer.jinwifidirect.repository.SocketUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Acer on 22/3/2017.
 */
public class ServerSendMessageThread implements Runnable
{
    private ServerSocket serverSocket;
    private HostChatActivity activity;
    private boolean isConnected = true;

    public ServerSendMessageThread (HostChatActivity acti)
    {
        activity = acti;
    }

    @Override
    public void run()
    {
        try {
            serverSocket = new ServerSocket(8888);

            while(true) {
                try {
                    Socket serverClient = serverSocket.accept();
                    SocketUtil.getInstance().storeSocket(serverClient);

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
            Log.d("TAG","Hell");
            serverSocket.close();
            SocketUtil.getInstance().setEmptySocket();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setIsConnected(Boolean b)
    {
        isConnected = b;
    }


}
