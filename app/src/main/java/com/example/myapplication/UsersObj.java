package com.example.myapplication;

public class UsersObj
{
    public String email;
    public String password;

    public UsersObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsersObj( String email,String password)
    {
        this.email = email;
        this.password=password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}