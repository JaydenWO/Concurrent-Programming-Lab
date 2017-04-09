package com.example.acer.jinwifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;;
import com.example.acer.jinwifidirect.repository.SocketUtil;
import java.io.IOException;
import java.io.PrintWriter;

public class HostChatActivity extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    private ServerReceiveMessageThread serverReceiveMessageThread = null;
    private ServerSendMessageThread serverSendMessageThread = null;
    private boolean left;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_chat);
        final ListView listView = (ListView)findViewById(R.id.listView3);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    public void hostForceDisconnect()
    {
        mManager.removeGroup(mChannel, null);
        chatArrayAdapter.clear();
        Intent i = new Intent(HostChatActivity.this, HostActivity.class);
        startActivity(i);
    }

    public void updateReceiveChat(String message)
    {
        left=false;//left side
        chatArrayAdapter.add(new ChatMessage(message,left));
    }

    public void updateSendChat(String message)
    {
        left=true;//right side
        EditText messageET = (EditText)findViewById(R.id.editText3);
        chatArrayAdapter.add(new ChatMessage(message,left));
        messageET.setText("");
    }

    public void disconnectChat (View v)
    {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                chatArrayAdapter.clear();
                Intent i = new Intent(HostChatActivity.this, HostActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(int reasonCode) {
                chatArrayAdapter.clear();
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new HostChatBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, intentFilter);//new object broadcast
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void serverSendMessage(View v)
    {
        EditText messageET = (EditText)findViewById(R.id.editText3);
        String message = messageET.getText().toString();
        PrintWriter pw = null;
        if (messageET.getText().toString().trim().length() != 0) {
            try {
                pw = new PrintWriter(SocketUtil.getInstance().getSocket().getOutputStream(), true);//null object
                pw.println(message);
                updateSendChat(message);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHostName(String s) {
        TextView messageTV = (TextView) findViewById(R.id.textView11);
        messageTV.setText(s);
    }

    public void serverSendingMessage()
    {
        //sendThread = new ServerSendMessageThread(8867, this, messageQueue);
        serverSendMessageThread = new ServerSendMessageThread(this);
        Thread t = new Thread(serverSendMessageThread);
        t.start();
    }

    public void serverReceivingMessage()
    {
        serverReceiveMessageThread = new ServerReceiveMessageThread(this);
        Thread t = new Thread(serverReceiveMessageThread);
        t.start();
    }

    public void hostSend (boolean b)
    {
        Button btn = (Button)findViewById(R.id.sendBtn);
        btn.setEnabled(b);
    }


    public void interrupt()
    {
        if(serverSendMessageThread != null) {
            serverSendMessageThread.setIsConnected(false);
            serverSendMessageThread.interruptSocket();
        }
        if(serverReceiveMessageThread != null)
        {
            serverReceiveMessageThread.setIsConnected(false);
            serverReceiveMessageThread.interruptSocket();
        }
    }
}
