package com.example.myapplication;


import java.io.Serializable;
import java.util.ArrayList;

public class FilterObj implements Serializable {
    private String destination;
    private int date_dep_start, date_dep_end;
    private ArrayList<String> flight_Purposes;


    public FilterObj(String destination,  int date_dep_start, int date_dep_end,ArrayList<String> flight_Purposes) {
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

    public int getDate_dep_end() {
        return date_dep_end;
    }


    public int getDate_dep_start() {
        return date_dep_start;
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
