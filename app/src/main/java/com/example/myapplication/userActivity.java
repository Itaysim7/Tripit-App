package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class userActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView not_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        not_register = (TextView) findViewById(R.id.register);
        not_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == not_register)
        {
            Intent intent=new Intent(this,RegisterActivity.class);
            startActivity(intent);
        }//if
    }
}