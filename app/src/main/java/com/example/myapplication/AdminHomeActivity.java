package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener{

    //FireBase/Store:
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    //Adapters for posts:
    private RecyclerView mFirestoreList;
    private AdapterHome adapter;

    private Button goToPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);

        goToPosts = findViewById(R.id.goToAdminPostsBtn);
        goToPosts.setOnClickListener(this);

        //Query for the posts that the admin did not approve yet
        Query query=db.collection("Posts").whereEqualTo("approval",true).orderBy("timestamp",Query.Direction.DESCENDING).limit(100);
        PagedList.Config config=new PagedList.Config.Builder().setInitialLoadSizeHint(8).setPageSize(2).build();

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

    }

    @Override
    public void onClick(View v) {
        if(v == goToPosts){
            Intent intent = new Intent(this, ApprovePostsActivity.class);
            startActivity(intent);
        }
    }

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
        }
        if(id==R.id.Search)
        {
            Intent intent=new Intent(this,SearchPostActivity.class);
            startActivity(intent);
        }
        if(id==R.id.home)
        {
            Intent intent=new Intent(this,homePage.class);
            startActivity(intent);
        }
        if(id==R.id.myProfile)
        {
            Intent intent=new Intent(this,ProfileActivity.class);
            startActivity(intent);
        }
        if(id == R.id.savePost){
            Intent intent=new Intent(this,FavPostsActivity.class);
            startActivity(intent);
        }
        if(id==R.id.logOut)
        {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



}