package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
    AdapterHome have the following functionality:
        1)Makes integration between DB to Home-Page activity at run-time.
        2)AdapterHome create the card template which filled with user information.
        3)Define hard-coded background.
        4)Listen for changes in DB and user interaction and update accordingly.
 */
public class AdapterHome extends FirestoreRecyclerAdapter<PostsModel,AdapterHome.ViewHolder>
{
    private UsersObj user_for_post;
    private Context context;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterHome(@NonNull FirestoreRecyclerOptions<PostsModel> options, Context context) {
        super(options);
        this.context=context;
    }//AdapterHome
    //Handling the binding steps.
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostsModel model)
    {
        //Create and set default display data
        holder.list_departure_date.setText("תאריך יציאה: "+model.getDeparture_date());
        holder.list_return_date.setText("תאריך חזרה: "+model.getReturn_date());
        holder.list_destination.setText("יעד: "+model.getDestination());
        holder.list_gender.setText("מין: "+model.getGender());
        holder.list_description.setText("תיאור: "+model.getDescription());
        holder.list_type.setText("מטרות הטיול שלי: "+model.getType_trip());
        //set age
        int min_age=model.getMin_age();
        int max_age=model.getMax_age();
        if(min_age==-1&&max_age==-1)
            holder.list_age.setText("טווח גילאים: לא צוין ");
        if (min_age==-1)
            holder.list_age.setText("טווח גילאים: עד " + max_age);
        if (max_age==-1)
            holder.list_age.setText("טווח גילאים: לפחות " + min_age);
        if(min_age!=-1&&max_age!=-1)
            holder.list_age.setText("טווח גילאים: " + min_age+"-"+max_age);
        //Image for background by destination
        switch(model.getDestination())
        {
            case "ארצות הברית":
                holder.list_layout.setBackgroundResource(R.drawable.usa);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "גרמניה":
                holder.list_layout.setBackgroundResource(R.drawable.germany);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "צרפת":
                holder.list_layout.setBackgroundResource(R.drawable.paris);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "יוון":
                holder.list_layout.setBackgroundResource(R.drawable.polynesia);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "הפיליפינים":
                holder.list_layout.setBackgroundResource(R.drawable.maldives);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "הולנד":
                holder.list_layout.setBackgroundResource(R.drawable.holland);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "הממלכה המאוחדת":
                holder.list_layout.setBackgroundResource(R.drawable.london);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            case "איטליה":
                holder.list_layout.setBackgroundResource(R.drawable.italy);
                holder.list_layout.getBackground().setAlpha(80);
                break;
            default:
                holder.list_layout.getBackground().setAlpha(80);
                break;
        }//switch
        String user_id=model.getUser_id();
        //Set image for the post from profile imageURL
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                user_for_post = snapshot.getValue(UsersObj.class);
                //set fullName
                assert user_for_post != null;
                holder.list_fullName.setText(user_for_post.getFullName());
                //set image
                if (user_for_post.getImageUrl().equals("default")) {
                    Glide.with(context).load(R.drawable.profile).circleCrop().into(holder.list_image_url);
                }//if
                 else {
                    Glide.with(context).load(user_for_post.getImageUrl()).circleCrop().into(holder.list_image_url);
                }//else
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }//onCancelled
        });//addValueEventListener

        FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUser_id = myUser.getUid();
        DatabaseReference reference_user = FirebaseDatabase.getInstance().getReference("users").child(myUser_id);
        reference_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsersObj user = snapshot.getValue(UsersObj.class);
                    if (user.getFavPosts() != null) {
                        for (String key : user.getFavPosts().keySet()) {
                            if (user.getFavPosts().get(key).equals(model.getId())) {
                                holder.Star.setFavorite(true);
                            }//if
                        }//for
                    }//if
                }//onDataChange

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        holder.Star.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            buttonView.setFavorite(favorite);
                            DatabaseReference reference_userPosts = reference_user.child("favPosts");
                            com.google.firebase.database.Query query = reference_userPosts.orderByValue().equalTo(model.getId());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()){
                                        reference_userPosts.push().setValue(model.getId());
                                    }
                                }//OnDataChange

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    throw databaseError.toException();
                                }//onCancelled
                            });//addListenerForSingleValueEvent
                        }//if
                        else
                        {
                            buttonView.setFavorite(favorite);
                            DatabaseReference reference_userPosts = reference_user.child("favPosts");
                            com.google.firebase.database.Query query = reference_userPosts.orderByValue().equalTo(model.getId());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }//for
                                }//OnDataChange

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    throw databaseError.toException();
                                }//onCancelled
                            });//addListenerForSingleValueEvent
                        }//else
                    }//onFavoriteChanged
                });//setOnFavoriteChangeListener

    }//onBindViewHolder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_single,parent,false);
        return new AdapterHome.ViewHolder(v);
    }//onCreateViewHolder

    /**
        ViewHoder responsible for the creation of the layout variables such as:TextView,Imageview etc..
        and mapping each layout variable to component id.
    */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView list_fullName;
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        private ImageView list_image_url;
        private MaterialFavoriteButton Star;
        private RelativeLayout list_layout;
        //Constructor
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            list_fullName=itemView.findViewById(R.id.list_fullName);
            list_departure_date=itemView.findViewById(R.id.list_departure_date);
            list_return_date=itemView.findViewById(R.id.list_return_date);
            list_destination=itemView.findViewById(R.id.list_destination);
            list_age=itemView.findViewById(R.id.list_age);
            list_gender=itemView.findViewById(R.id.list_gender);
            list_description=itemView.findViewById(R.id.list_description);
            list_type=itemView.findViewById(R.id.list_type);
            list_image_url=itemView.findViewById(R.id.list_image_url);
            Star = itemView.findViewById(R.id.Star);
            list_layout = itemView.findViewById(R.id.list_layout);
        }
    }
}
