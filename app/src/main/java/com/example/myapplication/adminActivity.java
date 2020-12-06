package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "adminActivity";

    private FirebaseUser fUser;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private UsersObj user;

    private EditText emailEditText, passwordEditText;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //firebase variables
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        //find id
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        login = findViewById(R.id.login);

        //set OnClickListeners
        emailEditText.setOnClickListener(this);
        passwordEditText.setOnClickListener(this);
        login.setOnClickListener(this);

/*        /*reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", error.getMessage());
            }
        });*/


    }

    public void onClick(View v) {
        if (v == login) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter email or password", Toast.LENGTH_LONG).show();
                return;
            }//if
            else {
                validation(email, password);
            }//else - email and password is not empty
        }//else if
    }

    public void validation(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            fUser = mAuth.getCurrentUser();
                            updateUI(fUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(adminActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                        }//else
                    }
                });
    }//validation

    private void updateUI(FirebaseUser user) {
        Intent loginIntent = new Intent(this, AdminHomeActivity.class);
        startActivity(loginIntent);
    }//updateUI

}