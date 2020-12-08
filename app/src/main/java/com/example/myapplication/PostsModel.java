package com.example.myapplication;

import android.widget.Switch;

import com.google.firebase.Timestamp;

import java.lang.reflect.Array;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostsModel
{
    private Timestamp departure_date;
    private Timestamp return_date;
    private String destination;
    private String age;
    private String gender;
    private String description;
    private ArrayList<String> type_trip;

    private PostsModel(Timestamp departure_date, Timestamp return_date, String destination, String age, String gender, String description) {
        this.departure_date = departure_date;
        this.return_date = return_date;
        this.destination = destination;
        this.age = age;
        this.gender = gender;
        this.description = description;
    }

    //constructor
    private PostsModel()
    {

    }

    public void setType_trip(ArrayList<String> type_trip) {
        this.type_trip = type_trip;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeparture_date(Timestamp departure_date) {
        this.departure_date = departure_date;
    }

    public void setReturn_date(Timestamp return_date) {
        this.return_date = return_date;
    }

    public String getDeparture_date()
    {
        String dep=TimestampToString(departure_date);
        return dep;
    }

    public String getReturn_date()
    {
        String ret=TimestampToString(return_date);
        return ret;
    }
    public String getDestination() {
        return destination;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getDescription() {
        return description;
    }

    public String getType_trip()
    {
        String text="";
        if(type_trip!=null)
        {
            for(int i=0;i<type_trip.size();i++)
            {
                text=text+type_trip.get(i)+", ";
            }
            text=text.substring(0,text.length()-2);
        }
        return text;
    }

    private String TimestampToString(Timestamp time)
    {
        Date date=time.toDate();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String s = formatter.format(date);
        return s;
    }
}

