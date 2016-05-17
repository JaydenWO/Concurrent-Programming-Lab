package com.example.acer.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    Button testButton;
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testButton = (Button) findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView2);

        Person person = new Person();
        person.setName("CoderzHeaven");
        person.setAddress("CoderzHeaven India");
        person.setNumber("1234567890");

        //save the object
        saveObject(person);

        // Get the Object

        final Person person1 = (Person)loadSerializedObject(); //get the serialized object from the sdcard and caste it into the Person class.
        testButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                textView.setText(String.format("Name : " + person1.getName()));
            }
        });

    }

    public void saveObject(Person person){
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("appSaveState.data")); //Select where you wish to save the file...
            oos.writeObject(person); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
        }
        catch(Exception ex)
        {
            Log.v("Serialize Save Error : ",ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Object loadSerializedObject()
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("appSaveState.data"));
            Object o = ois.readObject();
            return o;
        }
        catch(Exception ex)
        {
            Log.v("Serialize Read Error : ",ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}