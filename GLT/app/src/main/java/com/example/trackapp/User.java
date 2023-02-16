package com.example.trackapp;

import java.util.SplittableRandom;
import android.location.Location;

public class User {
    public String name,email,group;
    public Location location;

    public User(){
    }

    public User(String name, String email, String group){
        this.name = name;
        this.email=email;
        this.location=new Location("");
        this.group=group;
        this.location.setLatitude(23.5678); //dummy location
        this.location.setLongitude(34.456);
    }
}
