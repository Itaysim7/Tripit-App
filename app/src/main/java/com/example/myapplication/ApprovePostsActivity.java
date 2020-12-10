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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ApprovePostsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView mFirestoreList;
    private FirestorePagingAdapter adapter;
    private DatabaseReference reference;
    private CollectionReference DocRef;
    private FirebaseAuth mAuth;
    private String id;

    private TextView yes_txt;
    private TextView no_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_posts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        mAuth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference("users");
        db=FirebaseFirestore.getInstance();
        DocRef = db.collection("Posts");
        mFirestoreList=findViewById(R.id.firestore_list);


        //Query for the posts that admin did not approve yet
        Query query=db.collection("Posts").whereNotEqualTo("approval",1);
        PagedList.Config config=new PagedList.Config.Builder().setInitialLoadSizeHint(8).setPageSize(2).build();

        //recyclerOptions
        FirestorePagingOptions<PostsModel> options=new FirestorePagingOptions.Builder<PostsModel>()
                .setQuery(query,config,PostsModel.class).build();
        adapter= new FirestorePagingAdapter<PostsModel, PostsViewHolder>(options) {
            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_to_approve,parent,false);
                return new PostsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull PostsModel model)
            { //set data
                holder.list_departure_date.setText("תאריך יציאה: "+model.getDeparture_date());
                holder.list_return_date.setText("תאריך חזרה: "+model.getReturn_date());
                holder.list_destination.setText("יעד: "+model.getDestination());
                holder.list_age.setText("גיל: "+model.getAge());
                holder.list_gender.setText("מין: "+model.getGender());
                holder.list_description.setText("תיאור: "+model.getDescription());
                holder.list_type.setText("מטרות הטיול: "+model.getType_trip());

                holder.yes_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setApproved(1);
                        db.collection("Posts").document(model.getId()).set(model);
                    }
                });
                holder.no_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //reference= FirebaseDatabase.getInstance().getReference("users").child(model.getId()).child("myPosts");
                        //reference.setValue()
                        db.collection("Posts").document(model.getId()).delete();
                    }
                });
            }
        };


        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }

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
        if(id==R.id.logOut)
        {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private class PostsViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG="RegisterActivity";
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        private TextView yes_txt;
        private TextView no_txt;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_departure_date = itemView.findViewById(R.id.list_departure_date);
            list_return_date = itemView.findViewById(R.id.list_return_date);
            list_destination = itemView.findViewById(R.id.list_destination);
            list_age = itemView.findViewById(R.id.list_age);
            list_gender = itemView.findViewById(R.id.list_gender);
            list_description = itemView.findViewById(R.id.list_description);
            list_type = itemView.findViewById(R.id.list_type);

            yes_txt = itemView.findViewById(R.id.yes_txt);
            no_txt = itemView.findViewById(R.id.no_txt);
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
}