package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class represents the activity "search post".
 * In this class, the user is able search for posts, by entering the destination, departure date and the purposes.
 * The user will be brought back to the home page, but will see only the posts that match his wishes.
 *
 * There are two different ways to pick the departure date:
 * Range - The user will pick two dates, and the departure date will be any date in that range.
 * Specific - The user will pick a single date, and the posts that have that date as the departure date will be shown.
 */
public class SearchPostActivity extends AppCompatActivity implements View.OnClickListener  {
    private CountryCodePicker country_picker;
    private ArrayList<String> flight_Purposes;
    private int date_dep_start;
    private int date_dep_end;
    private FirebaseAuth mAuth;
    Button btn_date_specific, btn_date_range,  btn_trip_type, btn_search;
    Calendar c_start_end;
    DatePickerDialog dp_start_end;
    MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> build_range;
    MaterialDatePicker<androidx.core.util.Pair<Long, Long>> pick_range;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        TextView page_name = toolbar.findViewById(R.id.page_name);
        page_name.setText("חיפוש");
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        //findView
        country_picker = findViewById(R.id.autocomp_destination);
        btn_date_specific = findViewById(R.id.search_date_specific_input);
        btn_date_range = findViewById(R.id.search_date_range_input);
        btn_trip_type = findViewById(R.id.btn_trip_type);
        btn_search = findViewById(R.id.btn_search);

        //Listeners:
        btn_date_specific.setOnClickListener(this);
        btn_date_range.setOnClickListener(this);
        btn_trip_type.setOnClickListener(this);
        btn_search.setOnClickListener(this);

        //FireBase:
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        //Init Filter Variables
        flight_Purposes = new ArrayList<String>();
        date_dep_start = Integer.MIN_VALUE;
        date_dep_end = Integer.MAX_VALUE;
    }//onCreate

    //when clicking the "return" button on the phone
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
    }

    //------------------------------toolbar options------------------------------
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

    @Override
    public void onClick(View v) {
        if (v == btn_date_specific) // Case of specific date
        {
            c_start_end = Calendar.getInstance();
            int day = c_start_end.get(Calendar.DAY_OF_MONTH);
            int month = c_start_end.get(Calendar.MONTH);
            int year = c_start_end.get(Calendar.YEAR);

            dp_start_end = new DatePickerDialog(SearchPostActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth) {
                    String date = dayOfMonth + "/" + (mMonth + 1) + "/" + mYear;
                    btn_date_specific.setText(date);
                    btn_date_range.setText("גמיש בתאריכי היציאה");
                    date_dep_start = dayOfMonth+(mMonth+1)*100+ mYear*10000;
                    //Toast.makeText(getApplicationContext(), dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear, Toast.LENGTH_LONG).show();
                }//onDateSet
            }, year, month, day);
            dp_start_end.show();
        }//If v == btn_date_specific
        else if (v == btn_date_range) //Case of range of dates
        {
            build_range = MaterialDatePicker.Builder.dateRangePicker();
            build_range.setTitleText("בחר טווח תאריכים");
            pick_range = build_range.build();
            pick_range.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
            pick_range.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    Long start_time_Long = selection.first;
                    Date start_time_date = new Date(start_time_Long);
                    Long end_time_Long = selection.second;
                    Date end_time_date = new Date(end_time_Long);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                    String first_date = sdf2.format(start_time_date);
                    String end_date = sdf2.format(end_time_date);
                    if(first_date != null)
                        date_dep_start = Date_To_Int(first_date)-100;
                    if(end_date != null)
                        date_dep_end = Date_To_Int(end_date)-100;
                    btn_date_range.setText(end_date + "-" + first_date);
                    btn_date_specific.setText("תאריך מדוייק");
                    //Toast.makeText(getApplicationContext(), first_date+" "+end_date, Toast.LENGTH_LONG).show();
                }//onPositiveButtonClick
            });//addOnPositiveButtonClickListener

        }//else if v == btn_date_range
        else if (v == btn_trip_type) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchPostActivity.this,R.style.CustomAlertDialog);
            //string array for alert dialog multichoice items(flight Purposes)
            String[] flightPurposes = new String[]{"בטן-גב", "טרקים", "אומנות", "שופינג", "סקי", "טבע", "מורשת", "אחרי צבא", "קולינרי", "אחר"};
            //convert the flightPurposes array to list
            final List<String> flightPurposesList = Arrays.asList(flightPurposes);
            //boolean array for initial selected items(flight Purposes)
            final boolean[] checkedFlightPurposes = new boolean[]{false, false, false, false, false, false, false, false, false, false};
            //set alertDialog title
            builder.setTitle("בחר סוגי טיול");
            //set multichoice
            builder.setMultiChoiceItems(flightPurposes, checkedFlightPurposes, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    //update current focused item's checked status
                    checkedFlightPurposes[which] = isChecked;
                    //get the current focused item's
                    String currentItems = flightPurposesList.get(which);
                }//OnClick
            });//setMultiChoiceItems

            //set positive/yes button onclick listener
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    flight_Purposes.clear();
                    btn_trip_type.setText("מטרות הטיסה שבחרת:");
                    for (int i = 0; i < checkedFlightPurposes.length; i++) {
                        boolean checked = checkedFlightPurposes[i];
                        if (checked) {
                            btn_trip_type.setText(btn_trip_type.getText() + flightPurposesList.get(i) + " ");
                            flight_Purposes.add(flightPurposesList.get(i));
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

            AlertDialog dialog = builder.create();
            //show alert
            dialog.show();
        }//else if v == btn_trip_type
        else if(v == btn_search)
        {
            String destination = country_picker.getSelectedCountryName();
            FilterObj filter = new FilterObj(destination,date_dep_start,date_dep_end,flight_Purposes);
            Bundle bundle = new Bundle();
            bundle.putSerializable("filter", filter);
            Intent intent=new Intent(this,homePage.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }//else if v == btn_search
    }//onClick


    /**
     * Simple convert function from dd/mm/yyyy format to yyyymmdd Integer
     * @param date is the date to be converted
     * @return integer in format yyyymmdd
     */
    private int Date_To_Int(String date) {
        String[] partial = date.split("/");
        int day = Integer.parseInt(partial[0]);
        int month = Integer.parseInt(partial[1]);
        int year = Integer.parseInt(partial[2]);
        return (day+(month+1)*100+ year*10000);
    }//Date_To_Int





}

