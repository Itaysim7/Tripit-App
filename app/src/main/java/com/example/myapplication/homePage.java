package com.example.myapplication;

import androidx.annotation.NonNull;
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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;

/**
 * This class represents the activity "home page".
 * In this class, the user will be able to see other users' posts.
 * Only posts that have been approved by an admin will appear in the home page.
 */
public class homePage extends AppCompatActivity {
    //FireBase/Store:
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    //Saving Data of Users as objects:
    private UsersObj user;
    private String fullName;
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
        reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
                fullName=user.getFullName();
                //Hello Message:
                TextView helloTxt = findViewById(R.id.hello);
                helloTxt.setText("שלום " + fullName + " ממליצים לך לערוך את הפרופיל שלך");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.firestore_list);
        //Basic Query:
        Query query = db.collection("Posts").whereEqualTo("approval", true);//Query for the post that admin approve
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        boolean specific=true;
        //Compound Query By Premade Indexes: serializing queries by user inputs
        if (bundle != null)
        {
            page_name.setText("תוצאות החיפוש");
            FilterObj filter = (FilterObj) bundle.getSerializable("filter");
            String destination = filter.getDestination();
            if (destination != null) {
                query = query.whereEqualTo("destination", destination);
            }//if
            int date_dep_start = filter.getDate_dep_start();
            int date_dep_end = filter.getDate_dep_end();

            if (date_dep_end == Integer.MAX_VALUE && date_dep_start != Integer.MIN_VALUE) {//Specific
                query = query.whereEqualTo("departure_date", date_dep_start);
            }//if
            if (date_dep_end != Integer.MAX_VALUE && date_dep_start != Integer.MIN_VALUE) {//Not specific
                specific=false;
                query = query.whereGreaterThanOrEqualTo("departure_date", date_dep_start);
                query = query.whereLessThanOrEqualTo("departure_date", date_dep_end);
            }//else
            if (filter.get_Flight_Purposes() != null) {
                ArrayList<String> trip_type = new ArrayList<String>(filter.get_Flight_Purposes());
                query = query.whereArrayContainsAny("type_trip", trip_type);
            }//if
        }//if
        if(!specific)
            query=query.orderBy("departure_date",Query.Direction.ASCENDING).limit(100);
        else
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