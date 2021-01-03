package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class represents the "forgot password" activity.
 * In this class, the user can restore his forgotten password
 * by entering the e-mail he registered with.
 */
public class forgot_password extends AppCompatActivity implements View.OnClickListener {
    //Layout variables
    private TextInputEditText txtEmail;
    private Button btnSend, btnBack;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //Firestore:
        auth = FirebaseAuth.getInstance();
        //Find view by id
        txtEmail = findViewById(R.id.txtEmail);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        //Listeners:
        txtEmail.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View v) {
    if(v == btnSend)
        {
            if(!txtEmail.getText().toString().trim().isEmpty()){
                auth.sendPasswordResetEmail(txtEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "הססמא אופסה והנחיות נשלחו לאימייל", Toast.LENGTH_LONG).show();
                        }//if
                         else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }//else

                    }//onComplete
                } );//AddOnCompleteListeners

            }//if
            else {
                Toast.makeText(getApplicationContext(), "בבקשה הכנס/י את האימייל בו נרשמת", Toast.LENGTH_LONG).show();
            }//else
        }//if

        else if(v == btnBack)
        {
            Intent intent = new Intent(getApplicationContext(), userActivity.class);
            startActivity(intent);
        }//else if
    }//onClick
}