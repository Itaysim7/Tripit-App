package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 AdapterHome have the following functionality:
 1)Makes integration between DB to Home-Page activity at run-time.
 2)AdapterHome create the card template which filled with user information.
 3)Define hard-coded background.
 4)Listen for changes in DB and user interaction and update accordingly.
 */
public class AdapterApprove extends FirestoreRecyclerAdapter<PostsModel,AdapterApprove.ViewHolder>
{
    private Context context;
    private DatabaseReference reference;
    private FirebaseFirestore db;
    //Saving Data of Users as objects:
    private UsersObj user;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterApprove(@NonNull FirestoreRecyclerOptions<PostsModel> options, Context context)
    {
        super(options);
        this.context=context;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostsModel model)
    {
        //set data
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
        }
        String user_id=model.getUser_id();
        //Set image for the post from profile imageURL
        reference = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                user = snapshot.getValue(UsersObj.class);
                //set image
                if (user.getImageUrl().equals("default")) {
                    holder.list_image_url.setImageResource(R.drawable.user_image);
                }
                else {
                    Glide.with(context).load(user.getImageUrl()).into(holder.list_image_url);
                }
            }//onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //approve post
        holder.yes_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Posts").document(model.getId()).update("approval",true);
            }
        });
        holder.no_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Posts").document(model.getId()).delete();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_to_approve,parent,false);
        return new AdapterApprove.ViewHolder(v);
    }   //onCreateViewHolder



/*
      ViewHoder responsible for the creation of the layout variables such as:TextView,Imageview etc..
      and mapping each layout variable to component id.
  */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private static final String TAG="RegisterActivity";
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        private ImageView list_image_url;
        private TextView yes_txt;
        private TextView no_txt;
        private RelativeLayout list_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            list_departure_date = itemView.findViewById(R.id.list_departure_date);
            list_return_date = itemView.findViewById(R.id.list_return_date);
            list_destination = itemView.findViewById(R.id.list_destination);
            list_age = itemView.findViewById(R.id.list_age);
            list_gender = itemView.findViewById(R.id.list_gender);
            list_description = itemView.findViewById(R.id.list_description);
            list_type = itemView.findViewById(R.id.list_type);
            list_image_url=itemView.findViewById(R.id.list_image_url);
            list_layout = itemView.findViewById(R.id.list_layout);
            yes_txt = itemView.findViewById(R.id.yes_txt);
            no_txt = itemView.findViewById(R.id.no_txt);
        }//ViewHolder
    }//ViewHolder
}//AdapterApprove
