package com.example.acer.myapplication;

/**
 * Created by Acer on 17/5/2016.
 */
import java.io.Serializable;



import java.io.Serializable;

public class Person implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;
    private String name;
    private int age;


    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }
}




