package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreatePost extends AppCompatActivity implements View.OnClickListener
{
    private String age_text = "";
    TextView text_dep_date,text_ret_date,text_type_trip;
    Button btn_dep_date,btn_ret_date,btn_type_trip,btn_gender,btn_age;
    Calendar cal_dep,cal_ret;
    DatePickerDialog dpd_dep,dpd_ret;


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


        //Listener
        btn_ret_date.setOnClickListener(this);
        btn_dep_date.setOnClickListener(this);
        btn_type_trip.setOnClickListener(this);
        btn_gender.setOnClickListener(this);
        btn_age.setOnClickListener(this);


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
                    text_dep_date.setText(dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear);
                }
            },day,month,year);
            dpd_dep.show();
        }
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
                    text_ret_date.setText(dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear);
                }
            },day,month,year);
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
                    text_type_trip.setText("מטרות הטיסה שבחרת:");
                    for(int i=0;i<checked_flight_purposes.length;i++)
                    {
                        boolean checked=checked_flight_purposes[i];
                        if(checked)
                        {
                            text_type_trip.setText(text_type_trip.getText()+flight_purposes_List.get(i)+" ");
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
            String [] list_gender=new String[]{"נקבה","זכר","לא מעוניין לענות"};
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(CreatePost.this);
            mBuilder.setTitle("בחר מין");
            mBuilder.setSingleChoiceItems(list_gender, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btn_gender.setText(list_gender[which]);
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
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(CreatePost.this);
            mBuilder.setTitle("הכנס גיל");
            final EditText age_input=new EditText(this);
            // Specify the type of input expected
            age_input.setInputType(InputType.TYPE_CLASS_NUMBER);
            mBuilder.setView(age_input);
            // Set up the buttons
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    age_text = age_input.getText().toString();
                    btn_age.setText(age_text);
                }
            });
            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            mBuilder.show();
        }
    }


}


