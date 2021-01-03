package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
/**
    AdapterProfile have the following functionality:
        1)Makes integration between DB to Profile activity at run-time.
        2)AdapterProfile create the card template which filled with user information.
        3)Define hard-coded background.
        4)Listen for changes in DB and user interaction and update accordingly.
 */

public class AdapterProfile  extends FirestoreRecyclerAdapter<PostsModel,AdapterProfile.ViewHolder>
{
    private FirebaseFirestore db;
    private Context context;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterProfile(@NonNull FirestoreRecyclerOptions<PostsModel> options, Context context)
    {
        super(options);
        this.context=context;
        db = FirebaseFirestore.getInstance();
    }//AdapterProfile
    //Handling the binding steps.
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostsModel model)
    {
        holder.list_departure_date.setText("תאריך יציאה: " + model.getDeparture_date());
        holder.list_return_date.setText("תאריך חזרה: " + model.getReturn_date());
        holder.list_destination.setText("יעד: " + model.getDestination());
        holder.list_gender.setText("מין: " + model.getGender());
        holder.list_description.setText("תיאור: " + model.getDescription());
        holder.list_type.setText("מטרות הטיול שלי: " + model.getType_trip());
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
        holder.edit_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openDialog(model);
            }//onClick
        });//setOnClickListener
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Posts").document(model.getId()).delete();
            }//onClick
        });//setOnClickListener

    }//onBindViewHolder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_items_in_profile,parent,false);
        return new ViewHolder(v);
    }//onCreateViewHolder
    public void openDialog(PostsModel model)
    {
        EditPostsDialog dialog = new EditPostsDialog(model);
        dialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "Edit Post");
    }//openDialog

    /*
       ViewHoder responsible for the creation of the layout variables such as:TextView,Imageview etc..
       and mapping each layout variable to component id.
   */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;
        private Button edit_btn, delete_btn;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            list_departure_date=itemView.findViewById(R.id.list_departure_date);
            list_return_date=itemView.findViewById(R.id.list_return_date);
            list_destination=itemView.findViewById(R.id.list_destination);
            list_age=itemView.findViewById(R.id.list_age);
            list_gender=itemView.findViewById(R.id.list_gender);
            list_description=itemView.findViewById(R.id.list_description);
            list_type=itemView.findViewById(R.id.list_type);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }//ViewHolder
    }//PostViewr
}
