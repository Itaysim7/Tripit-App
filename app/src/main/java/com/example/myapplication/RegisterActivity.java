package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * This class represents the "Register" activity.
 * In this class, the user can register to the app by entering his full name, e-mail, and password.
 * The data is then saved into the database.
 */
public class RegisterActivity extends AppCompatActivity
{
    private EditText emailEditText,passwordEditText,password2EditText,fullNameEditText;
    private Button register_now_btn;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private UsersObj user;
    private static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameEditText= findViewById(R.id.fullname);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        password2EditText = findViewById(R.id.repeat_password);
        register_now_btn = findViewById(R.id.register_now);


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        reference =database.getReference("users");
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
                    String fullName=fullNameEditText.getText().toString();
                    if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(fullName))
                    {
                        Toast.makeText(getApplicationContext(),"אתה חייב למלא את כלל השדות",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(password.length()<6)
                    {
                        Toast.makeText(getApplicationContext(),"אורך הסיסמה חייב להיות לפחות 6",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String password2=password2EditText.getText().toString();
                    if(!password.equals(password2))//if the password different
                    {
                        Toast.makeText(getApplicationContext(),"הסיסמאות אינן תואמות, נסה שוב",Toast.LENGTH_LONG).show();
                        return;
                    }
                    user=new UsersObj(email,"default","empty", fullName,"default", 0,false);
                    registerUser(email,password);
                }
            }
        });

    }//onCreate

    /**
     * This function creates a new user in the firebase authentication, with the e-mail and password the user entered.
     * @param email is the e-mail the user entered.
     * @param password is the user's password.
     */
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
                            Toast.makeText(RegisterActivity.this, "הרשמה נכשלה. נסה שנית.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * This function adds the user data to the realtime database,
     * and moves the user from this activity to the home activity if the registration was successful.
     * @param firebaseUser is the current user.
     */
    public void updateUI(FirebaseUser firebaseUser)
    {
        reference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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