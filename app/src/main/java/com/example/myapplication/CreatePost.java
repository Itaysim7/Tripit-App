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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreatePost extends AppCompatActivity implements View.OnClickListener
{
    TextView tDepDate,tRetDate,tTypeTrip;
    Button btnDepDate,btnRetDate,btnTypeTrip;
    Calendar cDep,cRet;
    DatePickerDialog dpdDep,dpdRet;


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

        //button
        tDepDate=findViewById(R.id.return_date);
        btnRetDate=findViewById(R.id.buttonForDepartureDate);
        btnRetDate.setOnClickListener(this);

        tRetDate=findViewById(R.id.departure_date);
        btnDepDate=findViewById(R.id.buttonForReturneDate);
        btnDepDate.setOnClickListener(this);

        tTypeTrip=findViewById(R.id.flightPurposes);
        btnTypeTrip=findViewById(R.id.buttonForFlightPurposes);
        btnTypeTrip.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }

    @Override
    public void onClick(View view)
    {
        if(view==btnDepDate)
        {
            cDep=Calendar.getInstance();
            int day=cDep.get(Calendar.DAY_OF_MONTH);
            int month=cDep.get(Calendar.MONTH);
            int year=cDep.get(Calendar.YEAR);
            dpdDep=new DatePickerDialog(CreatePost.this, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    tDepDate.setText(dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear);
                }
            },day,month,year);
            dpdDep.show();
        }
        else if(view==btnRetDate)
        {
            cRet=Calendar.getInstance();
            int day=cRet.get(Calendar.DAY_OF_MONTH);
            int month=cRet.get(Calendar.MONTH);
            int year=cRet.get(Calendar.YEAR);
            dpdRet=new DatePickerDialog(CreatePost.this, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    tRetDate.setText(dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear);
                }
            },day,month,year);
            dpdRet.show();
        }
        else if(view==btnTypeTrip)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(CreatePost.this);
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
                }
            });

            //set positive/yes button onclick listener
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    tTypeTrip.setText("מטרות הטיסה שבחרת:");
                    for(int i=0;i<checkedFlightPurposes.length;i++)
                    {
                        boolean checked=checkedFlightPurposes[i];
                        if(checked)
                        {
                            tTypeTrip.setText(tTypeTrip.getText()+flightPurposesList.get(i)+" ");
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


    }


}


