package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreatePost extends AppCompatActivity implements View.OnClickListener
{
    private String age_text = "לא צוין",departure_date="",return_date="",gender="לא משנה",current_user_id="",min_age_string="",max_age_string="";
    private EditText description;
    private CountryCodePicker ccp;
    private Uri post_image_uri;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fUser;
    private UsersObj user;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private TextView text_dep_date,text_ret_date,text_type_trip;
    private Button btn_dep_date,btn_ret_date,btn_type_trip,btn_gender,btn_age,btn_publish;
    private Calendar cal_dep,cal_ret;
    private DatePickerDialog dpd_dep,dpd_ret;
    private ArrayList<String> type_array;
    private int dep_date=-1,ret_date=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        //firebase
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", error.getMessage());
            }
        });
        current_user_id=firebaseAuth.getCurrentUser().getUid();

        //progressDialog
        pd=new ProgressDialog(this);
        //CountryCodePicker
        ccp=findViewById(R.id.ccp);
        //EditText
        description=findViewById(R.id.description);
        //TextView
        text_dep_date=findViewById(R.id.departure_date);
        text_ret_date=findViewById(R.id.return_date);
        text_type_trip=findViewById(R.id.flight_purposes);
        //button
        btn_dep_date=findViewById(R.id.btn_for_departure_Date);
        btn_ret_date=findViewById(R.id.btn_for_return_Date);
        btn_type_trip=findViewById(R.id.btn_for_flight_purposes);
        btn_gender=findViewById(R.id.btn_for_gender);
        btn_age=findViewById(R.id.btn_for_age);
        btn_publish=findViewById(R.id.publish);

        //Listener for button
        btn_ret_date.setOnClickListener(this);
        btn_dep_date.setOnClickListener(this);
        btn_type_trip.setOnClickListener(this);
        btn_gender.setOnClickListener(this);
        btn_age.setOnClickListener(this);
        btn_publish.setOnClickListener(this);


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
        if(id == R.id.savePost){
            Intent intent=new Intent(this,FavPostsActivity.class);
            startActivity(intent);
        }
        if(id==R.id.logOut)
        {
            firebaseAuth.signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        if(view==btn_dep_date)
        {
            cal_dep=Calendar.getInstance();
            int day=cal_dep.get(Calendar.DAY_OF_MONTH);
            int month=cal_dep.get(Calendar.MONTH);
            int year=cal_dep.get(Calendar.YEAR);
            dpd_dep=new DatePickerDialog(CreatePost.this, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    departure_date=dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear;
                    text_dep_date.setText(departure_date);
                    dep_date =dayOfMonth+(mMonth+1)*100+ mYear*10000;
                }//onDateSet
            },year,month,day);
            dpd_dep.show();
        }//if
        else if(view==btn_ret_date)
        {
            cal_ret=Calendar.getInstance();
            int day=cal_ret.get(Calendar.DAY_OF_MONTH);
            int month=cal_ret.get(Calendar.MONTH);
            int year=cal_ret.get(Calendar.YEAR);
            dpd_ret=new DatePickerDialog(CreatePost.this, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    return_date=dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear;
                    text_ret_date.setText(return_date);
                    ret_date =dayOfMonth+(mMonth+1)*100+ mYear*10000;
                }
            },year,month,day);
            dpd_ret.show();
        }
        else if(view==btn_type_trip)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(CreatePost.this);
            //string array for alert dialog multichoice items(flight Purposes)
            String [] flight_purposes=new String[]{"בטן-גב","טרקים","אומנות","שופינג","סקי","טבע","מורשת","אחרי צבא","קולינרי","אחר"};
            //convert the flightPurposes array to list
            final List<String> flight_purposes_List= Arrays.asList(flight_purposes);
            //boolean array for initial selected items(flight Purposes)
            final boolean [] checked_flight_purposes=new boolean[]{false,false,false,false,false,false,false,false,false,false};
            //set alertDialog title
            builder.setTitle("בחר סוגי טיול");
            //set multichoice
            builder.setMultiChoiceItems(flight_purposes, checked_flight_purposes, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked)
                {
                    //update current focused item's checked status
                    checked_flight_purposes[which]=isChecked;
                    //get the current focused item's
                    String currentItems=flight_purposes_List.get(which);
                }
            });
            //set positive/yes button onclick listener
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    type_array=new ArrayList<String>();
                    text_type_trip.setText("מטרות הטיסה שבחרת:");
                    for(int i=0;i<checked_flight_purposes.length;i++)
                    {
                        boolean checked=checked_flight_purposes[i];
                        if(checked)
                        {
                            text_type_trip.setText(text_type_trip.getText()+flight_purposes_List.get(i)+" ");
                            type_array.add(flight_purposes_List.get(i));
                        }
                    }
                }
            });
            //set neutral/cancel button click listener
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog=builder.create();
            //show alert
            dialog.show();
        }
        else if(view==btn_gender)
        {
            String [] list_gender=new String[]{"אישה", "גבר", "לא משנה"};
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(CreatePost.this);
            mBuilder.setTitle("בחר עם איזה מין אתה מעוניין לטייל");
            mBuilder.setSingleChoiceItems(list_gender, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    gender=list_gender[which];
                    btn_gender.setText(gender);
                    dialog.dismiss();
                }
            });
            mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog=mBuilder.create();
            mDialog.show();
        }
        else if(view==btn_age)
        {
            LayoutInflater factory = LayoutInflater.from(this);
            //text_entry is an Layout XML file containing two text field to display in alert dialog
            final View textEntryView = factory.inflate(R.layout.age_dialog, null);
            final EditText min_age = (EditText) textEntryView.findViewById(R.id.min_age);
            final EditText max_age = (EditText) textEntryView.findViewById(R.id.max_age);
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("הכנס טווח גילאים:              ").setView(textEntryView).setPositiveButton("אישור",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton)
                        {
                            min_age_string=min_age.getText().toString();
                            max_age_string=max_age.getText().toString();
                            age_text = min_age_string+"-"+max_age_string;
                            btn_age.setText(age_text);
                        }
                    }).setNegativeButton("ביטול",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton)
                        {
                            dialog.cancel();
                        }
                    });
            alert.show();
        }
        else if(view==btn_publish)
        {
            String dest=ccp.getSelectedCountryName();
            String desc=description.getText().toString();
            //check if the user insert departure date
            if(dep_date==-1)
            {
                Toast.makeText(CreatePost.this, "חייב להכניס תאריך יציאה", Toast.LENGTH_LONG).show();
                return;
            }
            //check if dep_date<ret_date
            if(ret_date!=-1&&ret_date<=dep_date)
            {
                Toast.makeText(CreatePost.this, "תאריך החזרה חייב להיות אחרי תאריך היציאה", Toast.LENGTH_LONG).show();
                return;
            }
            //check if min_age<max_age and if the user insert integer
            if(min_age_string!=""&&max_age_string!="")
            {
                try
                {
                    int min=Integer.parseInt( min_age_string );
                    int max=Integer.parseInt( max_age_string );
                    if(max<min)
                    {
                        Toast.makeText(CreatePost.this, "הגיל המינימלי חייב להיות קטן מהגיל המקסימלי", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                catch( Exception e )
                {
                    Toast.makeText(CreatePost.this, "שדה גיל אינו מספר", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            uploadData(dest,desc);
        }
    }

    /*
        The function upload the data of the post to the firebase
     */
    private void uploadData(String dest,String desc)
    {
        pd.setTitle("מוסיף את המידע למאגר");
        pd.show();
        //random id for each data to be stored
        String id= UUID.randomUUID().toString(); //Create Random Post ID
        Map<String,Object> post_map=new HashMap<>();
        post_map.put("id",id);
        post_map.put("approval",false);
        post_map.put("user_id",current_user_id);
        post_map.put("timestamp",System.currentTimeMillis());
        post_map.put("destination",dest);
        post_map.put("departure_date",dep_date);
        post_map.put("return_date",ret_date);
        post_map.put("age",age_text);
        post_map.put("gender",gender);
        post_map.put("type_trip",type_array);
        post_map.put("description",desc);
        post_map.put("clicks",0);

        //add this data
        db.collection("Posts").document(id).set(post_map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //this will be called when the data added successfully
                        pd.dismiss();
                        Toast.makeText(CreatePost.this,"Uploaded...",Toast.LENGTH_SHORT).show();
                        Intent home_page=new Intent(CreatePost.this,homePage.class);
                        startActivity(home_page);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //this will be called if there is any error while uploading
                        pd.dismiss();
                        Toast.makeText(CreatePost.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private long getTime()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long sum=minute+hour*100+day*10000+month*1000000+year*100000000;
        return sum;
    }


}