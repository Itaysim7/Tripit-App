package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
    Welcome activity simply separate the login activity between high privileged users and simple users.
 */
public class welcomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button btn_admin_login,btn_user_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //Find view by ID
        btn_admin_login = findViewById(R.id.adminButton);
        btn_user_login = findViewById(R.id.userButton);
        //Listeners
        btn_admin_login.setOnClickListener(this);
        btn_user_login.setOnClickListener(this);
    }//OnCreate



    @Override
    public void onClick(View v) {
        if(v == btn_admin_login)
        {
            Intent intent=new Intent(this,adminActivity.class);
            startActivity(intent);
        }//if
        else if(v== btn_user_login)
        {
            Intent intent=new Intent(this,userActivity.class);
            startActivity(intent);
        }//else if
    }//on CLick

}//welcomeActivity