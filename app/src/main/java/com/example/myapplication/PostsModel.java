package com.example.myapplication;


import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class PostsModel
{
    private int departure_date;
    private int return_date;
    private String destination;
    private String age;
    private String gender;
    private String description;
    private String id;
    private ArrayList<String> type_trip;
    private boolean approval;
    private int clicks;
    private long timestamp;
    private String user_id;

    private PostsModel(int departure_date, int return_date, String destination, String age, String gender, String description,
                       String id,ArrayList<String> type_trip,boolean approval,int clicks,long timestamp,String user_id) {
        this.departure_date = departure_date;
        this.return_date = return_date;
        this.destination = destination;
        this.age = age;
        this.gender = gender;
        this.description = description;
        this.approval =approval;
        this.type_trip=type_trip;
        this.id = id;
        this.clicks=clicks;
        this.timestamp=timestamp;
        this.user_id=user_id;
    }

    //constructor
    private PostsModel()
    {

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
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

    public void setDeparture_date(int departure_date) {
        this.departure_date = departure_date;
    }

    public void setReturn_date(int return_date) {
        this.return_date = return_date;
    }

    public String getDeparture_date()
    {
        String dep=intToStringDate(departure_date);
        return dep;
    }

    public int getDeparture_date_int(){
        return departure_date;
    }

    public String getReturn_date()
    {
        String ret=intToStringDate(return_date);
        return ret;
    }
    public int getReturn_date_int(){
        return return_date;
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
        else
            text="לא צוין";
        return text;
    }
    public boolean getApproval() {
        return approval;
    }

    public String getId() {
        return id;
    }

    public int getClicks() {
        return clicks;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    private String intToStringDate(int time)
    {
        if(time != -1)
        {
            String day=""+(time%100);
            time=time/100;
            String month=""+(time%100);
            time=time/100;//year
            String s =day+"/"+month+"/"+time;
            return s;
        }//if
        else
            return "";
    }


}