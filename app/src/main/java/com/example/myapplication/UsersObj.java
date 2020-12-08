package com.example.myapplication;

import android.media.Image;
import android.net.Uri;

public class UsersObj
{
    private String email;
    private String imageUrl;
    private String description;
    private String fullName;
    private int age;
    private String gender;
    private int admin;

    public UsersObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsersObj(String email, String imageUrl, String description, String fullName, String gender, int age, int admin)
    {
        this.email = email;
        this.imageUrl = imageUrl;
        this.description=description;
        this.fullName = fullName;
        this.gender = gender;
        this.age =age;
        this.admin = admin;
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

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}