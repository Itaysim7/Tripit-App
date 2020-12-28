package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EditPostsDialog extends AppCompatDialogFragment implements View.OnClickListener {
    private CountryCodePicker ccp;
    private int dep_date, ret_date;
    private TextView dep_date_txt, ret_date_txt, type_type_txt;
    private EditText min_age, max_age, description;
    private Button btn_gender, btn_type_trip;
    private String gender;
    private ArrayList<String> type_array;


    private EditPostsListener listener;
    private final PostsModel model;


    public EditPostsDialog(PostsModel model) {
        this.model = model;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_posts_dialog, null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        //Local Variables
        dep_date_txt = view.findViewById(R.id.departure_date);
        ret_date_txt = view.findViewById(R.id.return_date);
        ccp=view.findViewById(R.id.ccp);
        min_age = view.findViewById(R.id.min_age_edit_txt);
        max_age = view.findViewById(R.id.max_age_edit_txt);
        btn_gender = view.findViewById(R.id.gender_btn);
        description = view.findViewById(R.id.description_edit_txt);
        type_type_txt = view.findViewById(R.id.your_activities_txt);
        btn_type_trip = view.findViewById(R.id.choose_type_trip_btn);


        dep_date_txt.setText(model.getDeparture_date());
        ret_date_txt.setText(model.getReturn_date());
        btn_gender.setText(model.getGender());
        description.setText(model.getDescription());

        dep_date = model.getDeparture_date_int();
        ret_date = model.getReturn_date_int();
        gender = model.getGender();

        //OnClickListeners
        dep_date_txt.setOnClickListener(this);
        ret_date_txt.setOnClickListener(this);
        btn_gender.setOnClickListener(this);
        btn_type_trip.setOnClickListener(this);

        builder.setView(view)
                .setTitle("Edit post")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String location = ccp.getSelectedCountryName();
                        if(!location.equals(model.getDestination()))
                            listener.ChangeLocation(location, model.getId());

                        //check if dep_date<ret_date
                        if(ret_date<=dep_date){
                            Toast.makeText(getContext(), "תאריך החזרה חייב להיות אחרי תאריך היציאה", Toast.LENGTH_LONG).show();
                            dep_date = model.getDeparture_date_int();
                            ret_date = model.getReturn_date_int();
                        }

                        if(dep_date != model.getDeparture_date_int())
                            listener.ChangeDepartureDate(dep_date, model.getId());

                        if(ret_date != model.getReturn_date_int())
                            listener.ChangeReturnDate(ret_date, model.getId());

                        String age1 = min_age.getText().toString();
                        String age2 = max_age.getText().toString();
                        int min = 0, max = 0;
                        if(!age1.equals("") || !age2.equals("")) {
                            if (!age1.equals("")) {
                                try {
                                    min = Integer.parseInt(age1);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "שדה גיל אינו מספר", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            if (!age2.equals("")) {
                                try {
                                    max = Integer.parseInt(age2);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "שדה גיל אינו מספר", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            if (max < min) {
                                Toast.makeText(getContext(), "הגיל המינימלי חייב להיות קטן מהגיל המקסימלי", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (min < 16) {
                                Toast.makeText(getContext(), "הגיל המינימלי הוא 16", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (max > 120) {
                                Toast.makeText(getContext(), "הגיל המקסימלי הוא 120", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        listener.ChangeAges(age1,age2, model.getId());
                        listener.ChangeGender(gender, model.getId());

                        String new_description = description.getText().toString();
                        listener.ChangeDescription(new_description, model.getId());

                        listener.ChangeTripType(type_array, model.getId());
                        Refresh();
                    }
                });



        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if(v == dep_date_txt){
            Calendar cal_dep = Calendar.getInstance();
            int day   = cal_dep.get(Calendar.DAY_OF_MONTH);
            int month = cal_dep.get(Calendar.MONTH);
            int year  = cal_dep.get(Calendar.YEAR);
            //onDateSet
            DatePickerDialog dpd_dep = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth) {
                    String departure_date = dayOfMonth + "/" + (mMonth + 1) + "/" + mYear;
                    dep_date_txt.setText(departure_date);
                    dep_date = dayOfMonth + (mMonth + 1) * 100 + mYear * 10000;
                }//onDateSet
            }, year, month, day);
            dpd_dep.show();
        }//if v == dep_date_txt
        if(v == ret_date_txt){
            Calendar cal_ret = Calendar.getInstance();
            int day   = cal_ret.get(Calendar.DAY_OF_MONTH);
            int month = cal_ret.get(Calendar.MONTH);
            int year  = cal_ret.get(Calendar.YEAR);
            //onDateSet
            DatePickerDialog dpd_ret = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth) {
                    String return_date = dayOfMonth + "/" + (mMonth + 1) + "/" + mYear;
                    ret_date_txt.setText(return_date);
                    ret_date = dayOfMonth + (mMonth + 1) * 100 + mYear * 10000;
                }//onDateSet
            }, year, month, day);
            dpd_ret.show();
        }//if v == ret_date_txt
        else if(v == btn_gender) {
            String [] list_gender=new String[]{"אישה", "גבר", "לא משנה"};
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(getContext());
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
        if(v == btn_type_trip){
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
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
                    type_type_txt.setText("מטרות הטיסה שבחרת:");
                    for(int i=0;i<checked_flight_purposes.length;i++)
                    {
                        boolean checked=checked_flight_purposes[i];
                        if(checked)
                        {
                            type_type_txt.setText(type_type_txt.getText()+flight_purposes_List.get(i)+" ");
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
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditPostsListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement EditPostsDialog!");
        }
    }

    private void Refresh(){
        Intent intent = new Intent(this.getActivity().getIntent());
        startActivity(intent);
    }


    public interface EditPostsListener{
        void ChangeLocation(String location, String id);
        void ChangeReturnDate(int return_date, String id);
        void ChangeDepartureDate(int departure_date, String id);

        void ChangeGender(String newGender, String id);
        void ChangeAges(String StartAge, String EndAge, String id);

        void ChangeDescription(String Description, String id);
        void ChangeTripType(ArrayList<String> trip_types, String id);
    }
}
