package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private EditText emailEditText,passwordEditText,password2EditText;
    private Button register_now_btn;
    private FirebaseDatabase database;
    private DatabaseReference mDatebase;
    private FirebaseAuth mAuth;
    private UsersObj user;
    private static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText=(EditText)findViewById(R.id.email);
        passwordEditText=(EditText)findViewById(R.id.password);
        password2EditText=(EditText)findViewById(R.id.repeat_password);
        register_now_btn = (Button)findViewById(R.id.register_now);


        database=FirebaseDatabase.getInstance();
        mDatebase=database.getReference("users");
        mAuth=FirebaseAuth.getInstance();


        register_now_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(view == register_now_btn)
                {
                    String email=emailEditText.getText().toString();
                    String password=passwordEditText.getText().toString();
                    if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password))
                    {
                        Toast.makeText(getApplicationContext(),"Enter email or password",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(password.length()<6)
                    {
                        Toast.makeText(getApplicationContext(),"Password length must be at least 6",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String password2=password2EditText.getText().toString();
                    if(!password.equals(password2))//if the password different
                    {
                        Toast.makeText(getApplicationContext(),"These passwords did not match, please try again",Toast.LENGTH_LONG).show();
                        return;
                    }
                    user=new UsersObj(email,"default","empty", "default","default", 0,0);
                    registerUser(email,password);
                }
            }
        });

    }//onCreate

    private void registerUser(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> ()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else
                            {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser firebaseUser)
    {
        mDatebase.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent loginIntent=new Intent(RegisterActivity.this,userActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                }
            }
        });
    }



}