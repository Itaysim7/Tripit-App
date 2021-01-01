package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ApprovePostsActivity extends AppCompatActivity
{
    //FireBase/Store:
    private FirebaseFirestore db;
    private DatabaseReference reference;
    private CollectionReference DocRef;
    private FirebaseAuth mAuth;

    //Saving Data of Users as objects:
    private UsersObj user;

    //Adapters for posts:
    private RecyclerView mFirestoreList;
    private AdapterApprove adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_posts);
        //Toolbars:
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView page_name = toolbar.findViewById(R.id.page_name);
        page_name.setText("אישור פוסטים");
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        mAuth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference("users");
        db=FirebaseFirestore.getInstance();
        DocRef = db.collection("Posts");
        mFirestoreList=findViewById(R.id.firestore_list);

        //Query for the posts that admin did not approve yet
        Query query=db.collection("Posts").whereEqualTo("approval",false).orderBy("timestamp",Query.Direction.ASCENDING).limit(20);
        //recyclerOptions
        FirestoreRecyclerOptions<PostsModel> options = new FirestoreRecyclerOptions.Builder<PostsModel>()
                .setQuery(query, PostsModel.class).build();
        //Setting for recycleview: where filling the posts
        adapter=new AdapterApprove(options,ApprovePostsActivity.this);
        mFirestoreList = findViewById(R.id.firestore_list);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(ApprovePostsActivity.this));
        adapter.startListening();
        mFirestoreList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    return;
                }

                for(DocumentChange dc: value.getDocumentChanges()){
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    switch (dc.getType()){
                        case MODIFIED:
                            finish();
                            startActivity(getIntent());
                        case REMOVED:
                            finish();
                            startActivity(getIntent());
                    }

                }
            }
        });
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
    }//onOptionsItemSelected


}