package com.example.myapplication;


import com.google.firebase.Timestamp;

import java.io.Serializable;

public class FilterObj implements Serializable {
    private String destination,gender;
    private Timestamp date_dep_start, date_dep_end;


    public FilterObj(String destination, String gender, Timestamp date_dep_start, Timestamp date_dep_end) {
        this.destination = destination;
        this.gender = gender;
        this.date_dep_start = date_dep_start;
        this.date_dep_end = date_dep_end;
    }

    public Timestamp getDate_dep_end() {
        return date_dep_end;
    }

    public void setDate_dep_end(Timestamp date_dep_end) {
        this.date_dep_end = date_dep_end;
    }

    public Timestamp getDate_dep_start() {
        return date_dep_start;
    }

    public void setDate_dep_start(Timestamp date_dep_start) {
        this.date_dep_start = date_dep_start;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
