package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class userActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "userActivity";
    private static final int RC_SIGN_IN = 1;
    private static final String PREFS_NAME = "preferences";
    private static final String KEY_NAME = "key_username";
    private static final String KEY_PASS = "key_password";
    private static final String KEY_CHECKBOX = "key_checkbox";
    private String email,pass;
    private boolean checkBox = false;
    private SharedPreferences sp;
    private FirebaseDatabase database;
    private DatabaseReference mDatebase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;

    private TextView not_register;
    private EditText emailEditText, passwordEditText;
    private Button login;
    private CheckBox save_Credentials;

    private Button mButtonFacebook;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private UsersObj user;

    private Button signInButton;
    private GoogleSignInClient mGoogleSignInClient;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //find view
        not_register = (TextView) findViewById(R.id.register);
        emailEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        save_Credentials = (CheckBox)findViewById(R.id.checkBox);

        //Listeners
        not_register.setOnClickListener(this);
        emailEditText.setOnClickListener(this);
        passwordEditText.setOnClickListener(this);
        login.setOnClickListener(this);
        save_Credentials.setOnClickListener(this);
        loadPreferences();
        save_Credentials.setChecked(checkBox);

        //Database - Firebase
        database = FirebaseDatabase.getInstance();
        mDatebase = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        //For the facebook login:
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookLogin();
        mButtonFacebook = findViewById(R.id.buttonForFacebook);
        mButtonFacebook.setOnClickListener(this);

        //For the google login:
        signInButton = findViewById(R.id.buttonForGoogle);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == not_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }//if
        else if(v == save_Credentials)
        {
            checkBox = save_Credentials.isChecked();
        }//else if
        else if (v == login) {
            email = emailEditText.getText().toString();
            pass = passwordEditText.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(getApplicationContext(), "Enter email or password", Toast.LENGTH_LONG).show();
                return;
            }//if
            else {
                validation(email, pass);
            }//else - email and password is not empty
        }//else if
        else if (v == mButtonFacebook) {
            loginManager.logInWithReadPermissions(this,
                    Arrays.asList("email", "public_profile", "user_birthday"));
        }//else if

        else if(v == signInButton){
            signIn();
        }//else if
    }//OnClick
    @Override
    public void onPause() {
        super.onPause();
        savePreferences(checkBox);

    }
    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

    private void savePreferences(boolean checkBox) {
        sp = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // Edit and commit
        if(checkBox)
        {
            email = emailEditText.getText().toString();
            pass = passwordEditText.getText().toString();
            System.out.println("onPause saved email: " + email);
            System.out.println("onPause saved password: " + pass);
            editor.putString(KEY_NAME, email);
            editor.putString(KEY_PASS, pass);
        }//if
        else
        {
            editor.remove(KEY_NAME);
            editor.remove(KEY_PASS);
            editor.remove(KEY_CHECKBOX);
        }//else

        editor.putBoolean(KEY_CHECKBOX, this.checkBox);
        editor.commit();
    }

    private void loadPreferences() {

        sp = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        // Get value
        checkBox = sp.getBoolean(KEY_CHECKBOX,false);
        email = sp.getString(KEY_NAME, "");
        pass = sp.getString(KEY_PASS, "");
        emailEditText.setText(email);
        passwordEditText.setText(pass);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
            return;
        updateUI(currentUser);
    }

    public void validation(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(userActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                        }//else
                    }
                });
    }//validation

    private void updateUI(FirebaseUser user) {
        goToHomePage();
    }//updateUI


    private void goToHomePage() {
        Intent loginIntent = new Intent(this, homePage.class);
        startActivity(loginIntent);
    }//goToHomePage




    //-----------------------Login with facebook functions--------------------

    public void facebookLogin() {
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "OnSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.v("LoginScreen", "---onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // here write code when get error
                Log.v("LoginScreen", "----onError: " + error.getMessage());
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null)
                    updateUI(user);
                else
                    updateUI(null);
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };
    }

    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookToken " + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "sign in with credential: successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Log.d(TAG, "sign in with credential: failed", task.getException());
                    Toast.makeText(userActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }


    //-----------------------Login with Google functions--------------------

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }//signIn

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(userActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(userActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(userActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        register(user);
                    } else {
                        Toast.makeText(userActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(userActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }//FirebaseGoogleAuth

    public void register(FirebaseUser firebaseUser)
    {
        user = new UsersObj(firebaseUser.getEmail(),"default","empty","default","default",0,0, null);
        mDatebase.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent loginIntent=new Intent(userActivity.this, homePage.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                }
            }
        });
    }

}
