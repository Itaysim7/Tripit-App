package com.example.myapplication;

public class UsersObj
{
    public String email;
    public String password;
    public String username;

    public UsersObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsersObj(String username, String email,String password)
    {
        this.username = username;
        this.email = email;
        this.password=password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}