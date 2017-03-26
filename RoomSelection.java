package com.example.acer.jinwifidirect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RoomSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selection);
    }

    public void createRoom (View v)
    {
        Intent i = new Intent(RoomSelection.this, HostActivity.class);
        startActivity(i);
    }

    public void joinRoom (View v)
    {
        Intent i = new Intent(RoomSelection.this, ClientActivity.class);
        startActivity(i);
    }
}
