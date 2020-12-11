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
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class FavPostsActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView mFirestoreList;
    private FirestorePagingAdapter adapter;
    private UsersObj user;
    private Query query;

    private TextView fav_posts_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_posts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        fav_posts_txt = findViewById(R.id.fav_posts_txt);

        mAuth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser fUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mFirestoreList=findViewById(R.id.firestore_list);
        reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
                if(user.getFavPosts() == null){
                    fav_posts_txt.setText("לא קיימים פוסטים להצגה.");
                }else{
                    ArrayList<String> value = new ArrayList<>();
                    for (String key: user.getFavPosts().keySet()) {
                        value.add(user.getFavPosts().get(key));
                    }


                    query = db.collection("Posts").whereIn("id", value);
                    PagedList.Config config = new PagedList.Config.Builder().setInitialLoadSizeHint(8).setPageSize(2).build();

                    //recyclerOptions
                    FirestorePagingOptions<PostsModel> options = new FirestorePagingOptions.Builder<PostsModel>()
                            .setQuery(query, config, PostsModel.class).build();
                    adapter = new FirestorePagingAdapter<PostsModel, PostsViewHolder>(options) {
                        @NonNull
                        @Override
                        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
                            return new PostsViewHolder(view);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull PostsModel model) { //set data
                            holder.list_departure_date.setText("תאריך יציאה: "+model.getDeparture_date());
                            holder.list_return_date.setText("תאריך חזרה: "+model.getReturn_date());
                            holder.list_destination.setText("יעד: "+model.getDestination());
                            holder.list_age.setText("גיל השותף: "+model.getAge());
                            holder.list_gender.setText("מין השותף: "+model.getGender());
                            holder.list_description.setText("תיאור: "+model.getDescription());
                            holder.list_type.setText("מטרות הטיול: "+model.getType_trip());
                            holder.star.setVisibility(View.INVISIBLE);

                        }
                    };

                    mFirestoreList.setHasFixedSize(true);
                    mFirestoreList.setLayoutManager(new LinearLayoutManager(FavPostsActivity.this));
                    mFirestoreList.setAdapter(adapter);
                    adapter.startListening();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", error.getMessage());
            }
        });

    }

    //------------------------Toolbar functions-------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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


    //------------------------Posts Class---------------------------------------
    private class PostsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        private TextView star;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_departure_date=itemView.findViewById(R.id.list_departure_date);
            list_return_date=itemView.findViewById(R.id.list_return_date);
            list_destination=itemView.findViewById(R.id.list_destination);
            list_age=itemView.findViewById(R.id.list_age);
            list_gender=itemView.findViewById(R.id.list_gender);
            list_description=itemView.findViewById(R.id.list_description);
            list_type=itemView.findViewById(R.id.list_type);
            star = itemView.findViewById(R.id.Star);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}