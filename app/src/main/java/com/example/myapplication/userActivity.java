package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class userActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="userActivity";
    private FirebaseDatabase database;
    private DatabaseReference mDatebase;
    private FirebaseAuth mAuth;

    private TextView not_register;
    private EditText emailEditText,passwordEditText;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //find view
        not_register = (TextView) findViewById(R.id.register);
        emailEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login) ;

        //Listeners
        not_register.setOnClickListener(this);
        emailEditText.setOnClickListener(this);
        passwordEditText.setOnClickListener(this);
        login.setOnClickListener(this);

        //Database - Firebase
        database=FirebaseDatabase.getInstance();
        mDatebase=database.getReference("users");
        mAuth= FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if(v == not_register)
        {
            Intent intent=new Intent(this,RegisterActivity.class);
            startActivity(intent);
        }//if
        else if(v == login)
        {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            {
                Toast.makeText(getApplicationContext(),"Enter email or password",Toast.LENGTH_LONG).show();
                return;
            }//if
            else
            {
                validation(email,password);
            }//else - email and password is not empty
        }//else if
    }//OnClick

    public void validation(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                            {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(userActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }//else
                    }
                });
    }//validation

    private void updateUI(FirebaseUser user) {
        goToHomePage();
    }//updateUI

    private void goToHomePage() {
        Intent loginIntent=new Intent(this,homePage.class);
        startActivity(loginIntent);
    }//goToHomePage
}