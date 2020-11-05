package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class welcomeActivity extends AppCompatActivity
{
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btn=(Button)findViewById(R.id.userButton);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openUser();
            }
        });
        btn=(Button)findViewById(R.id.adminButton);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openAdmin();
            }
        });
    }
    public void openUser()
    {
        Intent intent=new Intent(this,userActivity.class);
        startActivity(intent);
    }
    public void openAdmin()
    {
        Intent intent=new Intent(this,adminActivity.class);
        startActivity(intent);
    }


}