package com.example.myapplication;
/*
    Simple Class that represent the User Object with all his fields.
 */

import java.util.Date;
import java.util.HashMap;

public class UsersObj {
    private String email;
    private String imageUrl;
    private String description;
    private String fullName;
    private String gender;
    private int age;
    private Date birthday;
    private boolean admin;
    private HashMap<String, String> favPosts;

    public UsersObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsersObj(String email, String imageUrl, String description, String fullName, String gender, Date date, boolean admin) {
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
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}