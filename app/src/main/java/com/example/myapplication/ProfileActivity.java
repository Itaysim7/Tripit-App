package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
/**
 Profile Activity represent the About of the user.
 Profile Activity have the following functionality:
    1)Add profile picture given by the camera,phone memory or delete id necessary.
    2)Basic information of the user like name and description.
    3)All his posts that was created by him.
    4)Listen for changes and update accordingly.
    5)Handle ScrollView content updates at runtime.
 */
public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener, EditPostsDialog.EditPostsListener,EditProfileDialog.EditProfileListener {

    private static final String TAG = "ProfileActivity";

    private DatabaseReference reference;
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView mFirestoreList;
    private Query query;

    //-------------------Custom Objects---------------\\
    private AdapterProfile adapter;
    private UsersObj user;

    //--------------Layout Variables----------------\\
    private ImageView image_profile;
    private TextView about_myself_txt,myPosts,name_age_txt,change_image_txt;
    private Button edit_profile_btn;
    private FloatingActionButton change_image_btn;
    private ScrollView mScrollView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        image_profile = findViewById(R.id.image_profile_view);
        about_myself_txt = findViewById(R.id.About_Myself_Txt);
        myPosts = findViewById(R.id.My_Posts);
        name_age_txt = findViewById(R.id.name_and_age_txt);
        edit_profile_btn = findViewById(R.id.Edit_profile_btn);
        change_image_txt= findViewById(R.id.change_picture_txt);
        change_image_btn = findViewById(R.id.change_picture_btn);
        mScrollView = findViewById(R.id.scrollView);

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());

        //firebase
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        mFirestoreList=findViewById(R.id.firestore_list);

        db = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                user = snapshot.getValue(UsersObj.class);
                //Set name
                String intro = user.getFullName();
                if(user.getAge() != 0){
                    intro = intro + ", " + user.getAge();
                }//if
                name_age_txt.setText(intro);
                //set image
                if(user != null && !user.getImageUrl().equals("default")) {
                    image_profile.setBackgroundColor(Color.WHITE);
                    Glide.with(ProfileActivity.this).load(user.getImageUrl()).apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(25))).into(image_profile);
                    change_image_txt.setVisibility(View.INVISIBLE);
                    change_image_btn.setVisibility(View.VISIBLE);
                }//if
                else{
                    image_profile.setImageResource(R.color.grey);
                    change_image_txt.setVisibility(View.VISIBLE);
                    change_image_btn.setVisibility(View.INVISIBLE);
                }//else

                //set description
                if(user.getDescription().equals("empty")) {
                    about_myself_txt.setText("למשתמש זה אין שום דבר לומר על עצמו.");
                }//if
                 else{
                    about_myself_txt.setText(user.getDescription());
                }//else
                //find the posts this user uploaded
                query = db.collection("Posts").whereEqualTo("user_id", fUser.getUid()).whereEqualTo("approval",true).orderBy("timestamp",Query.Direction.DESCENDING).limit(100);
                //Check if the user uploaded posts. If not, change the text.
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }//if
                        if(value.isEmpty())
                            myPosts.setText("לא קיימים פוסטים להצגה.");
                    }//onEvent
                });//addSnapshotListener
                //recyclerOptions
                FirestoreRecyclerOptions<PostsModel> options = new FirestoreRecyclerOptions.Builder<PostsModel>()
                        .setQuery(query, PostsModel.class).build();
                //Setting for recycleview: where filling the posts
                adapter=new AdapterProfile(options,ProfileActivity.this);
                mFirestoreList = findViewById(R.id.firestore_list);
                mFirestoreList.setHasFixedSize(true);
                mFirestoreList.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                adapter.startListening();
                mFirestoreList.setAdapter(adapter);
            }//onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("מסד הנתונים נכשל.", error.getMessage());
            }//onCancelled
        });//addValueEventListener

        //variables for the photo upload
        change_image_txt.setOnClickListener(this);
        edit_profile_btn.setOnClickListener(this);
        change_image_btn.setOnClickListener(this);

        //Toolbar configuration
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView page_name = toolbar.findViewById(R.id.page_name);
        page_name.setText("הפרופיל שלי");
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

    }//onCreate


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }//onStop

    @Override
    protected void onStart() {
        super.onStart();
    }

    //when clicking the "return" button on the phone
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }//onBackPressed

    @Override
    public void onClick(View v) {
        if (v == change_image_txt || v == change_image_btn){
            BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
            bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
        }//if

        if(v == edit_profile_btn){
            openEditProfileDialog();
        }//of
    }//onClick

    private void openEditProfileDialog() {
        EditProfileDialog dialog = new EditProfileDialog(user);
        dialog.show(getSupportFragmentManager(), "Edit Profile");
    }//openEditProfileDialog

    //------------------------Toolbar functions-------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        //menu item click handling
        if(id==R.id.newPost)
        {
            Intent intent=new Intent(this,CreatePost.class);
            startActivity(intent);
        }//if
        else if(id==R.id.Search)
        {
            Intent intent=new Intent(this,SearchPostActivity.class);
            startActivity(intent);
        }//else if
        else if(id==R.id.home)
        {
            Intent intent=new Intent(this,homePage.class);
            startActivity(intent);
        }//else if
        else if(id==R.id.myProfile)
        {
            Intent intent=new Intent(this,ProfileActivity.class);
            startActivity(intent);
        }//else if
        else if(id == R.id.savePost){
            Intent intent=new Intent(this,FavPostsActivity.class);
            startActivity(intent);
        }//else if
        else if(id==R.id.logOut)
        {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
            startActivity(intent);
        }//else if
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected


    //---------------------------Methods for the posts dialog-------------------------
    @Override
    public void ChangeLocation(String location, String id) {
        db.collection("Posts").document(id).update("destination",location);
    }//ChangeLocation

    @Override
    public void ChangeReturnDate(int return_date, String id) {
        db.collection("Posts").document(id).update("return_date",return_date);
    }//ChangeReturnDate

    @Override
    public void ChangeDepartureDate(int departure_date, String id) {
        db.collection("Posts").document(id).update("departure_date",departure_date);
    }//ChangeDepartureDate

    @Override
    public void ChangeGender(String newGender, String id) {
        db.collection("Posts").document(id).update("gender", newGender);
    }//ChangeGender

    @Override
    public void ChangeAges(String StartAge, String EndAge, String id) {
        String range = StartAge+"-"+EndAge;
        db.collection("Posts").document(id).update("age",range);
    }//ChangeAges

    @Override
    public void ChangeDescription(String Description, String id) {
        if(Description.equals(""))
            Description = "ספר על עצמך";
        db.collection("Posts").document(id).update("description", Description);
    }//ChangeDescription

    @Override
    public void ChangeTripType(ArrayList<String> trip_types, String id) {
        db.collection("Posts").document(id).update("type_trip", trip_types);
    }//ChangeTripType

    //----------------------------------Methods for the edit profile dialog---------------------

    @Override
    public void ChangeName(String newName) {
        reference.child("fullName").setValue(newName);
    }

    @Override
    public void ChangeGenderForProfile(String newGender) {
        reference.child("gender").setValue(newGender);
    }//ChangeGenderForProfile

    @Override
    public void ChangeProfileDescription(String newDescription) {
        reference.child("description").setValue(newDescription);
    }//ChangeProfileDescription

    @Override
    public void ChangeBirthdayDate(int date) {
        reference.child("birthday").setValue(date);
    }


    //------------------------Class Responsible for Changes when Scrolling----------------------
    private class ScrollPositionObserver implements ViewTreeObserver.OnScrollChangedListener {
        private final int mImageViewHeight;

        public ScrollPositionObserver() {
            mImageViewHeight = getResources().getDimensionPixelSize(R.dimen.contact_photo_height);
        }//ScrollPositionObserver

        @Override
        public void onScrollChanged() {
            int baseColor = getColor(R.color.primary);
            int scrollY = Math.min(Math.max(mScrollView.getScrollY(), 0), mImageViewHeight);
            float alpha = scrollY / (float) mImageViewHeight;

            // changing position of ImageView
            image_profile.setColorFilter(getColorWithAlpha(alpha, baseColor));

            //Don't let name disappear -
            int distance_txt = name_age_txt.getTop() - mScrollView.getScrollY();
            if(distance_txt < 0 )
                name_age_txt.offsetTopAndBottom(10);


        }//onScrollChanged

        public int getColorWithAlpha(float alpha, int baseColor) {
            int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
            int rgb = 0x00ffffff & baseColor;
            return a + rgb;
        }//getColorWithAlpha
    }//ScrollPositionObserver


}//ProfileActivity
