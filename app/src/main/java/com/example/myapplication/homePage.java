package com.example.myapplication;


import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

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
    private FirestorePagingAdapter adapter;
    private String uri;
    //Layout - variables:
    private TextView helloTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //Toolbars:
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title


        mAuth=FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", error.getMessage());
            }//onCancelled
        });
        //Hello Message:
        TextView helloTxt = findViewById(R.id.hello);
        helloTxt.setText("שלום "+fUser.getEmail()+" ממליצים לך לערוך את הפרופיל שלך");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);
        //Basic Query:
        Query query = db.collection("Posts").whereEqualTo("approval", 1);//Query for the post that admin approve
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        //Compound Query By Premade Indexes: serializing queries by user inputs
        if(bundle != null) {
            Toast.makeText(getApplicationContext(), "Inside", Toast.LENGTH_LONG).show();
            FilterObj filter = (FilterObj) bundle.getSerializable("filter");
            String destination = filter.getDestination();
            if(destination != null) {
                query = query.whereEqualTo("destination", destination);
            }//if
            int date_dep_start = filter.getDate_dep_start();
            int date_dep_end = filter.getDate_dep_end();

            if(date_dep_end == Integer.MAX_VALUE && date_dep_start != Integer.MIN_VALUE) {//Specific
                System.out.println("Specific");
                query = query.whereEqualTo("departure_date", date_dep_start);
            }//if
            if(date_dep_end != Integer.MAX_VALUE && date_dep_start != Integer.MIN_VALUE) {//Not specific
                System.out.println("Not Specific");
                query = query.whereGreaterThanOrEqualTo("departure_date", date_dep_start);
                query = query.whereLessThanOrEqualTo("departure_date", date_dep_end);
            }//else
            if(filter.get_Flight_Purposes() != null) {
                ArrayList<String> trip_type = new ArrayList<String>(filter.get_Flight_Purposes());
                System.out.println("Trip type:\t"+trip_type.toString());
                query = query.whereArrayContainsAny("type_trip", trip_type);
            }//if
        }//if

        //How it will displayed:
        PagedList.Config config=new PagedList.Config.Builder().setInitialLoadSizeHint(8).setPageSize(2).build();
        //recyclerOptions
        FirestorePagingOptions<PostsModel> options=new FirestorePagingOptions.Builder<PostsModel>()
                .setQuery(query,config,PostsModel.class).build();
        adapter= new FirestorePagingAdapter<PostsModel, PostsViewHolder>(options) {
            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new PostsViewHolder(view) ;
            }

            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull PostsModel model)
            { //set data
                holder.list_departure_date.setText("תאריך יציאה: "+model.getDeparture_date());
                holder.list_return_date.setText("תאריך חזרה: "+model.getReturn_date());
                holder.list_destination.setText("יעד: "+model.getDestination());
                holder.list_age.setText("גיל השותף: "+model.getAge());
                holder.list_gender.setText("מין השותף: "+model.getGender());
                holder.list_description.setText("תיאור: "+model.getDescription());
                holder.list_type.setText("מטרות הטיול: "+model.getType_trip());
                String user_id=model.getUser_id();
                //Set image for the post from profile imageURL
                reference = FirebaseDatabase.getInstance().getReference("users").child(user_id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(UsersObj.class);
                        //set image
                        if (user.getImageUrl().equals("default"))
                        {
                        }
                        else {
                            Glide.with(homePage.this).load(user.getImageUrl()).into(holder.list_image_url);
                        }
                    }//onDataChange
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //star


//                if(holder.user.getFavPosts() != null) {
//                    for (String key : holder.user.getFavPosts().keySet()) {
//                        if (holder.user.getFavPosts().get(key).equals(model.getId())) {
//                            holder.Star.setText("סומן בכוכב");
//                        }
//                    }
//                }
//                holder.Star.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(holder.Star.getText().equals("סמן בכוכב")) {
//                            holder.Star.setText("סומן בכוכב");
//                            reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid()).child("favPosts");
//                            reference.push().setValue(model.getId());
//                        }
//                    }
//                });
            }
        };//Adapter
        //Setting for recycleview: where filling the posts
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
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

    /*
        Inner class for Fitting the data for each posts that will present in the homepage
     */
    private class PostsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        private ImageView list_image_url;
        private TextView Star;
        private UsersObj user;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_departure_date=itemView.findViewById(R.id.list_departure_date);
            list_return_date=itemView.findViewById(R.id.list_return_date);
            list_destination=itemView.findViewById(R.id.list_destination);
            list_age=itemView.findViewById(R.id.list_age);
            list_gender=itemView.findViewById(R.id.list_gender);
            list_description=itemView.findViewById(R.id.list_description);
            list_type=itemView.findViewById(R.id.list_type);
            list_image_url=itemView.findViewById(R.id.list_image_url);
            Star = itemView.findViewById(R.id.Star);
        }//Constructor
    }//PostViewr

}