package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import java.util.Arrays;
/**
 user Activity represent the user page for login.
 user Activity have the following functionality:
    1)Allows login for all users.
    2)Makes sure that the login details match the details in the db.
    3)Allows login from google.
    4)Allows login from facebook.
 */
public class userActivity extends AppCompatActivity implements View.OnClickListener
{
    //Finals:
    private static final String TAG = "userActivity";
    private static final int RC_SIGN_IN = 1;
    //For Shared Preference - Username & Password
    private static final String PREFS_NAME = "preferences";
    private static final String KEY_NAME = "key_username";
    private static final String KEY_PASS = "key_password";
    private static final String KEY_CHECKBOX = "key_checkbox";
    //Globals:
    private String email,pass;
    private boolean checkBox = false;
    private int attempt = 1;
    //Shared Preference
    private SharedPreferences sp;
    //FireBase/Store
    private UsersObj user;
    private FirebaseDatabase database;
    private DatabaseReference m_datebase;
    private FirebaseAuth m_auth;
    private FirebaseAuth.AuthStateListener auth_state_listener;
    private AccessTokenTracker access_token_tracker;
    //Layout - connectivity
    private TextView not_register,forgot_password;
    private EditText email_EditText, password_EditText;
    private Button login, m_button_facebook, sign_in_button_google;
    private CheckBox save_Credentials;
    //Facebook - credential
    private CallbackManager call_back_manager;
    private LoginManager login_manager;
    //Google - credential
    private GoogleSignInClient m_google_signIn_client;
    //context
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //find view
        not_register = (TextView) findViewById(R.id.register);
        email_EditText = (EditText) findViewById(R.id.username);
        password_EditText = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        save_Credentials = (CheckBox)findViewById(R.id.checkBox);
        forgot_password = (TextView)findViewById(R.id.forgot_password);

        //Listeners
        not_register.setOnClickListener(this);
        email_EditText.setOnClickListener(this);
        password_EditText.setOnClickListener(this);
        login.setOnClickListener(this);
        save_Credentials.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
        //For save - credentials
        loadPreferences();
        save_Credentials.setChecked(checkBox);

        //Database - Firebase
        database = FirebaseDatabase.getInstance();
        m_datebase = database.getReference("users");
        m_auth = FirebaseAuth.getInstance();

        //For the facebook login:
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookLogin();
        m_button_facebook = findViewById(R.id.buttonForFacebook);
        m_button_facebook.setOnClickListener(this);

        //For the google login:
        sign_in_button_google = findViewById(R.id.buttonForGoogle);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        m_google_signIn_client = GoogleSignIn.getClient(this, gso);
        sign_in_button_google.setOnClickListener(this);
    }//onCreate

    @Override
    public void onClick(View v)
    {
        if (v == not_register)
        {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }//if
        else if(v == forgot_password)
        {
            Intent intent = new Intent(this, forgot_password.class);
            startActivity(intent);
        }//else if
        else if(v == save_Credentials)
        {
            checkBox = save_Credentials.isChecked();
        }//else if
        else if (v == login)
        {
            if(attempt<4)
            {
                attempt++;
                email = email_EditText.getText().toString();
                pass = password_EditText.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "הכנס/י אימייל וססמא", Toast.LENGTH_LONG).show();
                    return;
                }//if
                else {
                    validation(email, pass);
                }//else - email and password is not empty
            }//if
            else
            {
                Toast.makeText(getApplicationContext(), "הינך חסום זמנית עקב ניסיונות כושלים להתחבר.", Toast.LENGTH_LONG).show();
                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished)
                    {
                       // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        //here you can have your logic to set text to edittext
                    }
                    public void onFinish()
                    {
                        attempt = 1;
                        Toast.makeText(getApplicationContext(), "את/ה יכל/ה לנסות להיכנס עכשיו.", Toast.LENGTH_LONG).show();
                    }
                }.start();

            }//4 More than 4 attempts
        }//else if
        else if (v == m_button_facebook)
        {
            login_manager.logInWithReadPermissions(this,
                    Arrays.asList("email", "public_profile", "user_birthday"));
        }//else if

        else if(v == sign_in_button_google)
        {
            signIn_withGoogle();
        }//else if
    }//OnClick
    @Override

    public void onPause() {
        super.onPause();
        savePreferences(checkBox);
    }//onPause

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }//onResume

    /*
      * The function Save the preference - user credentials
      * @param checkBox- if the user wants to save his preference
     */
    private void savePreferences(boolean checkBox)
    {
        sp = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // Edit and commit
        if(checkBox)//If checkbox was clicked
        {
            email = email_EditText.getText().toString();
            pass = password_EditText.getText().toString();
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
    }//savePreference

    /*
        The function Loading data from SharedPreference
     */
    private void loadPreferences()
    {
        sp = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get value
        checkBox = sp.getBoolean(KEY_CHECKBOX,false);
        email = sp.getString(KEY_NAME, "");
        pass = sp.getString(KEY_PASS, "");
        email_EditText.setText(email);
        password_EditText.setText(pass);
    }//loadPreferences

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //For Facebook:
        call_back_manager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        //Google:
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }//if
    }//onActivityResult



    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = m_auth.getCurrentUser();
        if(currentUser == null)
            return;
        updateUI(currentUser);
    }//onStart

    public void validation(String email, String password) {
        m_auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = m_auth.getCurrentUser();
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
        login_manager = LoginManager.getInstance();
        call_back_manager = CallbackManager.Factory.create();

        login_manager.registerCallback(call_back_manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "OnSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }//onSuccess

            @Override
            public void onCancel() {
                Log.v("LoginScreen", "---onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // here write code when get error
                Log.v("LoginScreen", "----onError: " + error.getMessage());
            }//onError
        });//facebookLogin

        auth_state_listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null)
                    updateUI(user);
                else
                    updateUI(null);
            }//onAuthStateChanged
        };//AuthStateListener

        access_token_tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    m_auth.signOut();
                }//if
            }//onCurrentAccessTokenChanged
        };//AccessTokenTracker
    }//facebookLogin

    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookToken " + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        m_auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "sign in with credential: successful");
                    FirebaseUser user = m_auth.getCurrentUser();
                    updateUI(user);
                }//if
                else{
                    Log.d(TAG, "sign in with credential: failed", task.getException());
                    Toast.makeText(userActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }//else
            }//onComplete
        });
    }


    //-----------------------Login with Google functions--------------------

    private void signIn_withGoogle() {
        Intent signInIntent = m_google_signIn_client.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }//signIn

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(userActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }//try
        catch (ApiException e){
            Toast.makeText(userActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }//catch
    }//handleSignInResult

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            m_auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(userActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = m_auth.getCurrentUser();
                        if(m_datebase.child(user.getUid()) == null)
                            register(user);
                        Intent loginIntent=new Intent(userActivity.this, homePage.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                        finish();
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

    public void register(FirebaseUser firebaseUser) {
        user = new UsersObj(firebaseUser.getEmail(),"default","empty","default","default", 0,false);
        m_datebase.child(firebaseUser.getUid()).setValue(user);
    }

}
