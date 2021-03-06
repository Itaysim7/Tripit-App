package com.example.myapplication;
/**
 *   Simple Class that represents the User Object with all his fields.
 */

import java.util.Calendar;
import java.util.HashMap;

public class UsersObj {
    private String email;
    private String imageUrl;
    private String description;
    private String fullName;
    private String gender;
    private int birthday;
    private boolean admin;
    private HashMap<String, String> favPosts;

    public UsersObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsersObj(String email, String imageUrl, String description, String fullName, String gender, int date, boolean admin) {
        this.email = email;
        this.imageUrl = imageUrl;
        this.description = description;
        this.fullName = fullName;
        this.gender = gender;
        this.birthday = date;
        this.admin = admin;
        this.favPosts = null;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long timeNow = day + (month+1)*100 + year*10000;
        if(this.getBirthday() == 0)
            return 0;
        int Age = (int) (timeNow - this.getBirthday());
        return Age/10000;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public HashMap<String, String> getFavPosts() {
        return favPosts;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFavPosts(HashMap<String, String> favPosts) {
        this.favPosts = favPosts;
    }

    public int getBirthday() {
        return this.birthday;
    }

    public String getBirthday_String(){
        String bday = intToStringDate(this.birthday);
        return bday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }


    /**
     * The function get int the represent birthday
     * @param time is the date of the integer birthday to be converted
     * @return birthday in format of string day/month/time
     */
    private String intToStringDate(int time) {
        if(time != -1) {
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