package com.example.myapplication;

import android.media.Image;
import android.net.Uri;

public class UsersObj
{
    public String email;
    public String password;
    private String imageUrl;

    public UsersObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsersObj(String email, String imageUrl)
    {
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}