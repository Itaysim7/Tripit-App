package com.example.myapplication;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SearchPostActivity extends AppCompatActivity implements View.OnClickListener  {
    private CountryCodePicker counry_picker;
    private ArrayList<String> picked_age,flight_Purposes;
    private String gender;
    private Timestamp date_dep_start, date_dep_end;
    private String destination;
    private boolean if_date_range_picked = false;
    Button btn_date_specific, btn_date_range, btn_range_age, btn_trip_type, btn_search, btn_gender;
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
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

        //findView

        counry_picker = (CountryCodePicker) findViewById(R.id.autocomp_destination);
        btn_date_specific = (Button) findViewById(R.id.search_date_specific_input);
        btn_date_range = (Button) findViewById(R.id.search_date_range_input);
        btn_gender = (Button) findViewById(R.id.btn_gender);
        btn_range_age = (Button) findViewById(R.id.btn_age_range);
        btn_trip_type = (Button) findViewById(R.id.btn_trip_type);
        btn_search = (Button) findViewById(R.id.btn_search);

        //Listeners:
        counry_picker.setOnClickListener(this);
        btn_date_specific.setOnClickListener(this);
        btn_date_range.setOnClickListener(this);
        btn_gender.setOnClickListener(this);
        btn_range_age.setOnClickListener(this);
        btn_trip_type.setOnClickListener(this);
        btn_search.setOnClickListener(this);

        //FireBase:
        db=FirebaseFirestore.getInstance();

        //Init Filter Variables
        gender = "";
        picked_age = new ArrayList<String>();
        flight_Purposes = new ArrayList<String>();
    }//onCreate

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
        }//if
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
    public void onClick(View v) {
        if(v == counry_picker)
        {
            destination = counry_picker.getSelectedCountryName();
        }//If
        else if (v == btn_date_specific) // Case of specific date
        {
            if_date_range_picked =false;
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
                    c_start_end.set(mYear,mMonth,dayOfMonth,0,0,0);
                    date_dep_start = new Timestamp(c_start_end.getTime());

                    //Toast.makeText(getApplicationContext(), dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear, Toast.LENGTH_LONG).show();
                }//onDateSet
            }, day, month, year);
            dp_start_end.show();
        }//If
        else if (v == btn_date_range) //Case of range of dates
        {
            if_date_range_picked = true;
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
                    date_dep_start = new Timestamp(start_time_date);
                    date_dep_end = new Timestamp(end_time_date);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                    //picked_dates = Date_Range_To_DatesList(start_time_date,end_time_date,sdf2);

                    String first_date = sdf2.format(start_time_date);
                    String end_date = sdf2.format(end_time_date);
                    btn_date_range.setText(end_date + "-" + first_date);
                    btn_date_specific.setText("תאריך מדוייק");
                    //Toast.makeText(getApplicationContext(), first_date+" "+end_date, Toast.LENGTH_LONG).show();
                }//onPositiveButtonClick
            });//addOnPositiveButtonClickListener

        }//else if
        else if (v == btn_trip_type) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchPostActivity.this);
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
        }//else if
        else if (v == btn_range_age) {
            picked_age.clear();
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchPostActivity.this);
            //string array for alert dialog multichoice items(age range)
            String[] age_vals = new String[]{"20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60", "60-65", "65-70"};
            //convert the flightPurposes array to list
            final List<String> ageList = Arrays.asList(age_vals);
            //boolean array for initial selected items(flight Purposes)
            final boolean[] checkedFlightPurposes = new boolean[]{false, false, false, false, false, false, false, false, false, false};
            //set alertDialog title
            builder.setTitle("בחר את טווחי הגילאים המתאימים לך");
            //set multichoice
            builder.setMultiChoiceItems(age_vals, checkedFlightPurposes, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    //update current focused item's checked status
                    checkedFlightPurposes[which] = isChecked;
                    //get the current focused item's
                    String currentItems = ageList.get(which);
                }//OnClick
            });//setMultiChoiceItems

            //set positive/yes button onclick listener
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btn_range_age.setText("טווחי הגילאים שבחרת:");
                    for (int i = 0; i < checkedFlightPurposes.length; i++) {
                        boolean checked = checkedFlightPurposes[i];
                        if (checked) {
                            btn_range_age.setText(btn_range_age.getText() + ageList.get(i) + " ");
                            Age_Range_To_AgeList(ageList.get(i));
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
        }//else if
        else if (v == btn_gender) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchPostActivity.this);
            //string array for alert dialog multichoice items(flight Purposes)
            String[] gender_val = new String[]{"אישה", "גבר", "לא משנה"};
            //convert the flightPurposes array to list
            final List<String> genderList = Arrays.asList(gender_val);
            //boolean array for initial selected items(flight Purposes)

            //set alertDialog title
            builder.setTitle("עם איזה מין את/ה רוצה לטייל?");
            final int[] cheked_gender_vals = {0};
            //set multichoice
            builder.setSingleChoiceItems(gender_val, cheked_gender_vals[0], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //update current focused item's checked status
                    cheked_gender_vals[0] = which;
                    String currentItems = genderList.get(which);
                }
            });//setMultiChoiceItems

            //set positive/yes button onclick listener
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    btn_gender.setText("מין מועדף: ");
                    btn_gender.setText(btn_gender.getText() + genderList.get(cheked_gender_vals[0]));
                    gender = genderList.get(cheked_gender_vals[0]);
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
        }//else if

        else if(v == btn_search)
        {
            ArrayList<Pair<QueryDocumentSnapshot,Integer>> order = new ArrayList<Pair<QueryDocumentSnapshot,Integer>>();
            if(if_date_range_picked == false)
            {
                Query query = db.collection("Posts").whereEqualTo("destination",destination)
                        .whereEqualTo("gender",gender)
                        .whereEqualTo("departure_date", date_dep_start);
                Intent intent = new Intent();

                System.out.println(query.toString());
            }//If
            else //Range of date
            {
                CollectionReference posts = db.collection("Posts");
                Task<QuerySnapshot> query = posts.whereEqualTo("destination",destination)
                        .whereEqualTo("gender",gender)
                        .whereGreaterThanOrEqualTo("departure_date", date_dep_start)
                        .whereLessThanOrEqualTo("departure_date", date_dep_end).get();
                Intent intent=new Intent(this,homePage.class);
                FilterObj filter = new FilterObj(destination,gender,date_dep_start,date_dep_start);
                intent.putExtra("filter",filter);
                startActivity(intent);
            }//else

        }//else if
    }//onClick

    /*
        From Range to List of Dates
     */
    public static ArrayList<String> Date_Range_To_DatesList(
            Date startDate, Date endDate, SimpleDateFormat sdf2) {
        ArrayList<String> datesInRange = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            String result = sdf2.format(calendar.getTime());
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }//while
        //While Skipping last day
        String result = sdf2.format(calendar.getTime());
        datesInRange.add(result);
        calendar.add(Calendar.DATE, 1);
        return datesInRange;
    }

    /*
        From Range to List of ages
     */
    private void Age_Range_To_AgeList(String s) {
        int start = Integer.valueOf(s.substring(0,2));
        int end = Integer.valueOf(s.substring(3,5));
        for (int i = start ; i<= end ; ++i)
        {
            picked_age.add(String.valueOf(i));
        }//for
    }//Age_Range_Refuctoring


}

