package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
/**
    AdminHomeActivity have the following functionality:
        1)Makes integration between User to Admin with higher privileges.
        2)Handling new posts approvement.
        3)Showing the posts that was approved sorted by upload date.
 */
public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener{

    //FireBase/Store:
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    //Adapters for posts:
    private RecyclerView mFirestoreList;
    private AdapterHome adapter;
    //Layout Variables
    private Button go_To_Posts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView page_name = toolbar.findViewById(R.id.page_name);
        page_name.setText("דף הבית");
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);

        go_To_Posts = findViewById(R.id.goToAdminPostsBtn);
        go_To_Posts.setOnClickListener(this);

        //Query for the posts that the admin did not approved yet
        Query query=db.collection("Posts").whereEqualTo("approval",true).orderBy("timestamp",Query.Direction.DESCENDING).limit(100);
        //recyclerOptions
        FirestoreRecyclerOptions<PostsModel> options = new FirestoreRecyclerOptions.Builder<PostsModel>()
                .setQuery(query, PostsModel.class).build();
        //Setting for recycleview: where filling the posts
        adapter=new AdapterHome(options,AdminHomeActivity.this);
        mFirestoreList = findViewById(R.id.firestore_list);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(AdminHomeActivity.this));
        adapter.startListening();
        mFirestoreList.setAdapter(adapter);

    }//onCreate

    @Override
    public void onClick(View v) {
        if(v == go_To_Posts){
            Intent intent = new Intent(this, ApprovePostsActivity.class);
            startActivity(intent);
        }//if
    }//onClick

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }//onStop

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }//onStart


    //---------------------ToolBar functions--------------------------------

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
        }//if
        else if(id==R.id.home)
        {
            Intent intent=new Intent(this,homePage.class);
            startActivity(intent);
        }//if
        else if(id==R.id.myProfile)
        {
            Intent intent=new Intent(this,ProfileActivity.class);
            startActivity(intent);
        }//if
        else if(id == R.id.savePost){
            Intent intent=new Intent(this,FavPostsActivity.class);
            startActivity(intent);
        }//if
        else if(id==R.id.logOut)
        {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
            startActivity(intent);
        }//if
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected



}//AdminHomeActivity