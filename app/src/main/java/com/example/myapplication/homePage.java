package com.example.myapplication;


import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;

public class homePage extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView mFirestoreList;
    private FirestorePagingAdapter adapter;
    private TextView helloTxt;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        helloTxt = findViewById(R.id.hello);
        mAuth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        helloTxt.setText("שלום "+user.getEmail()+" ממליצים לך לערוך את הפרופיל שלך");

        db=FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);

        query=db.collection("Posts").whereEqualTo("approval",1);//Query for the post that admin approve
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            Toast.makeText(getApplicationContext(), "Inside", Toast.LENGTH_LONG).show();
            FilterObj filter = (FilterObj) bundle.getSerializable("filter");
            String destination = filter.getDestination();
            if(destination != null) {
                System.out.println("Destination:\t"+destination);
                query = query.whereEqualTo("destination", destination);
            }
            Timestamp date_dep_start = filter.getDate_dep_start();

            Timestamp date_dep_end = filter.getDate_dep_end();
            if(date_dep_end == null) {//Specific
                System.out.println("Start:\t"+date_dep_start.toDate().toString());
                query = query.whereEqualTo("departure_date", date_dep_start);
            }//if
            else {//Not specific
                System.out.println("Start:\t"+date_dep_start.toDate().toString());
                System.out.println("End:\t"+date_dep_end.toDate().toString());
                query = query.whereGreaterThanOrEqualTo("departure_date", date_dep_start);
                query = query.whereLessThanOrEqualTo("departure_date", date_dep_end);
            }//if
            if(filter.get_Flight_Purposes() != null) {
                ArrayList<String> trip_type = new ArrayList<String>(filter.get_Flight_Purposes());
                System.out.println("Trip type:\t"+trip_type.toString());
                query = query.whereArrayContainsAny("type_trip", trip_type);
            }//if

        }//if
        PagedList.Config config=new PagedList.Config.Builder()
                .setInitialLoadSizeHint(8).setPageSize(2).build();
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

    private class PostsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        public PostsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            list_departure_date=itemView.findViewById(R.id.list_departure_date);
            list_return_date=itemView.findViewById(R.id.list_return_date);
            list_destination=itemView.findViewById(R.id.list_destination);
            list_age=itemView.findViewById(R.id.list_age);
            list_gender=itemView.findViewById(R.id.list_gender);
            list_description=itemView.findViewById(R.id.list_description);
            list_type=itemView.findViewById(R.id.list_type);
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
}