package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SearchPostActivity extends AppCompatActivity implements View.OnClickListener {
TextView start_end;
AutoCompleteTextView dest;
Button date_specific,date_range,gender,range_age,trip_type,search;
Calendar c_start,c_end,c_start_end;
DatePickerDialog dp_start,dp_end,dp_start_end;
MaterialDatePicker.Builder builder;
MaterialDatePicker materialDatePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        //findView
        dest =(AutoCompleteTextView) findViewById(R.id.autocomp_destination);
        date_specific =(Button) findViewById(R.id.search_date_specific_input);
        date_range = (Button)findViewById(R.id.search_date_range_input);
        gender = (Button)findViewById(R.id.btn_gender);
        range_age = (Button)findViewById(R.id.btn_age_range);
        trip_type = (Button)findViewById(R.id.btn_trip_type);
        search = (Button)findViewById(R.id.btn_search);

        //Listeners:
        dest.setOnClickListener(this);
        date_specific.setOnClickListener(this);
        date_range.setOnClickListener(this);
        gender.setOnClickListener(this);
        range_age.setOnClickListener(this);
        trip_type.setOnClickListener(this);
        search.setOnClickListener(this);

        //DatePicket Configuration
        builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("בחר טווח תאריכים");
        materialDatePicker = builder.build();

    }//onCreate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public void onClick(View v) {
        if(v == date_specific) // Case of specific date
        {
            c_start_end=Calendar.getInstance();
            int day=c_start_end.get(Calendar.DAY_OF_MONTH);
            int month=c_start_end.get(Calendar.MONTH);
            int year=c_start_end.get(Calendar.YEAR);
            dp_start_end=new DatePickerDialog(SearchPostActivity.this, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    //start_end.setText(dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear);
                    Toast.makeText(getApplicationContext(), dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear, Toast.LENGTH_LONG).show();
                }
            },day,month,year);
            dp_start_end.show();
        }//If
        else if(v == date_range) //Case of range of dates
        {
            materialDatePicker.show(getSupportFragmentManager(),"DATE_RANGE_PICKER");
        }//else if
        else if(v == trip_type)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(SearchPostActivity.this);
            //string array for alert dialog multichoice items(flight Purposes)
            String [] flightPurposes=new String[]{"בטן-גב","טרקים","אומנות","שופינג","סקי","טבע","מורשת","אחרי צבא","קולינרי","אחר"};
            //convert the flightPurposes array to list
            final List<String> flightPurposesList= Arrays.asList(flightPurposes);
            //boolean array for initial selected items(flight Purposes)
            final boolean [] checkedFlightPurposes=new boolean[]{false,false,false,false,false,false,false,false,false,false};
            //set alertDialog title
            builder.setTitle("בחר סוגי טיול");
            //set multichoice
            builder.setMultiChoiceItems(flightPurposes, checkedFlightPurposes, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked)
                {
                    //update current focused item's checked status
                    checkedFlightPurposes[which]=isChecked;
                    //get the current focused item's
                    String currentItems=flightPurposesList.get(which);
                }//OnClick
            });//setMultiChoiceItems

            //set positive/yes button onclick listener
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    trip_type.setText("מטרות הטיסה שבחרת:");
                    for(int i=0;i<checkedFlightPurposes.length;i++)
                    {
                        boolean checked=checkedFlightPurposes[i];
                        if(checked)
                        {
                            trip_type.setText(trip_type.getText()+flightPurposesList.get(i)+" ");
                        }//If
                    }//For
                }//onClick
            });//setPositiveButton

            //set neutral/cancel button click listener
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }//onClicl
            });//setNeutralButton

            AlertDialog dialog=builder.create();
            //show alert
            dialog.show();
        }//else if
    }//onClicl
}