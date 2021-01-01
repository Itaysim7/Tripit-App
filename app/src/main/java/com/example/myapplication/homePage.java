package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageTask;
import java.util.ArrayList;

public class homePage extends AppCompatActivity {
    //FireBase/Store:
    private FirebaseFirestore db;
    private Query query;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    //Upload Images:
    private StorageTask uploadTask;
    //Saving Data of Users as objects:
    private UsersObj user;
    //Adapters for posts:
    private RecyclerView mFirestoreList;
    private AdapterHome adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //Toolbars:
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView page_name = toolbar.findViewById(R.id.page_name);
        page_name.setText("דף הבית");
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        //Hello Message:
        TextView helloTxt = findViewById(R.id.hello);
        helloTxt.setText("שלום " + fUser.getEmail() + " ממליצים לך לערוך את הפרופיל שלך");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.firestore_list);
        //Basic Query:
        Query query = db.collection("Posts").whereEqualTo("approval", true);//Query for the post that admin approve
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //Compound Query By Premade Indexes: serializing queries by user inputs
        if (bundle != null) {
            Toast.makeText(getApplicationContext(), "Inside", Toast.LENGTH_LONG).show();
            FilterObj filter = (FilterObj) bundle.getSerializable("filter");
            String destination = filter.getDestination();
            if (destination != null) {
                query = query.whereEqualTo("destination", destination);
            }//if
            int date_dep_start = filter.getDate_dep_start();
            int date_dep_end = filter.getDate_dep_end();

            if (date_dep_end == Integer.MAX_VALUE && date_dep_start != Integer.MIN_VALUE) {//Specific
                System.out.println("Specific");
                query = query.whereEqualTo("departure_date", date_dep_start);
            }//if
            if (date_dep_end != Integer.MAX_VALUE && date_dep_start != Integer.MIN_VALUE) {//Not specific
                System.out.println("Not Specific");
                query = query.whereGreaterThanOrEqualTo("departure_date", date_dep_start);
                query = query.whereLessThanOrEqualTo("departure_date", date_dep_end);
            }//else
            if (filter.get_Flight_Purposes() != null) {
                ArrayList<String> trip_type = new ArrayList<String>(filter.get_Flight_Purposes());
                System.out.println("Trip type:\t" + trip_type.toString());
                query = query.whereArrayContainsAny("type_trip", trip_type);
            }//if
        }//if
        query=query.orderBy("timestamp",Query.Direction.DESCENDING).limit(100);
        //recyclerOptions
        FirestoreRecyclerOptions<PostsModel> options = new FirestoreRecyclerOptions.Builder<PostsModel>()
                .setQuery(query, PostsModel.class).build();
        //Setting for recycleview: where filling the posts
        adapter=new AdapterHome(options,homePage.this);
        mFirestoreList = findViewById(R.id.firestore_list);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(homePage.this));
        adapter.startListening();
        mFirestoreList.setAdapter(adapter);

    }//oncreate

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    //when clicking the "return" button on the phone
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, welcomeActivity.class);
        startActivity(intent);
    }
    //---------------------ToolBar functions--------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //menu item click handling
        if (id == R.id.newPost) {
            Intent intent = new Intent(this, CreatePost.class);
            startActivity(intent);
        }
        if (id == R.id.Search) {
            Intent intent = new Intent(this, SearchPostActivity.class);
            startActivity(intent);
        }
        if (id == R.id.home) {
            Intent intent = new Intent(this, homePage.class);
            startActivity(intent);
        }
        if (id == R.id.myProfile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.savePost) {
            Intent intent = new Intent(this, FavPostsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.logOut) {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}