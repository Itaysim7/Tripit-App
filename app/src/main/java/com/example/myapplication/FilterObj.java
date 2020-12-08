package com.example.myapplication;


import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class FilterObj implements Serializable {
    private String destination;
    private Long date_dep_start, date_dep_end;
    private ArrayList<String> flight_Purposes;


    public FilterObj(String destination,  Long date_dep_start, Long date_dep_end,ArrayList<String> flight_Purposes) {
        this.destination = destination;
        this.date_dep_start = date_dep_start;
        this.date_dep_end = date_dep_end;
        this.flight_Purposes = new ArrayList<String>(flight_Purposes);

    }
    public ArrayList<String> get_Flight_Purposes() {
        if(flight_Purposes.isEmpty())
            return null;
        return flight_Purposes;
    }

    public Timestamp getDate_dep_end() {
        if(date_dep_end == null)
            return null;
        Date start_time_date = new Date(date_dep_end);
        Timestamp timestamp = new Timestamp(start_time_date);
        System.out.println(timestamp.toDate().toString());
        return timestamp;
    }


    public Timestamp getDate_dep_start() {
        if(date_dep_start == null)
            return null;
        Date start_time_date = new Date(date_dep_start);
        Timestamp timestamp = new Timestamp(start_time_date);
        System.out.println(timestamp.toDate().toString());
        return timestamp;
    }




    public String getDestination() {
        System.out.println(destination);
        return destination;
    }

    public String toString()
    {
        String s = "";
        s+="Dest:\t"+destination+"\n"+"Gender:\t"+"\n"+"Date_Dep:\t"+date_dep_start+"\n"+"Date_Ret:\t"+date_dep_end+"\n"+"Picked types:\t"+flight_Purposes.toString();
        return s;
    }
}
