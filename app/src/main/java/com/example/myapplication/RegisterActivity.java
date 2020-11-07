package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button register_now_btn;
    private TextView popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_now_btn = (Button)findViewById(R.id.register_now);
        register_now_btn.setOnClickListener(this);

    }//onCreate

    @Override
    public void onClick(View view)
    {
        if(view == register_now_btn)
        {
            popup.setText("ההרשמה התבצעה בהצלחה!");
            Intent intent=new Intent(this,userActivity.class);
            startActivity(intent);
        }//if
    }//onClick

}