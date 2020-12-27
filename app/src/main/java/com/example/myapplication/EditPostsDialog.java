package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.Calendar;

public class EditPostsDialog extends AppCompatDialogFragment implements View.OnClickListener {
    private TextView dep_date_txt, ret_date_txt;
    private EditPostsListener listener;
    private CountryCodePicker ccp;
    private Calendar cal_dep, cal_ret;
    private DatePickerDialog dpd_dep,dpd_ret;
    private int dep_date, ret_date;

    private final PostsModel model;

    private DatabaseReference reference;
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    public EditPostsDialog(PostsModel model) {
        this.model = model;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_posts_dialog, null);

        db = FirebaseFirestore.getInstance();

        builder.setView(view)
                .setTitle("Edit profile")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String location=ccp.getSelectedCountryName();
                        listener.ChangeLocation(location, model.getId());
                        listener.ChangeDepartureDate(dep_date, model.getId());
                        listener.ChangeReturnDate(ret_date, model.getId());
                    }
                });

        //Local Variables
        dep_date_txt = view.findViewById(R.id.departure_date);
        ret_date_txt = view.findViewById(R.id.return_date);
        ccp=view.findViewById(R.id.ccp);


        dep_date_txt.setText(model.getDeparture_date());
        ret_date_txt.setText(model.getReturn_date());

        //OnClickListeners
        dep_date_txt.setOnClickListener(this);
        ret_date_txt.setOnClickListener(this);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if(v == dep_date_txt){
            cal_dep = Calendar.getInstance();
            int day   = cal_dep.get(Calendar.DAY_OF_MONTH);
            int month = cal_dep.get(Calendar.MONTH);
            int year  = cal_dep.get(Calendar.YEAR);
            dpd_dep=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    String departure_date=dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear;
                    dep_date_txt.setText(departure_date);
                    dep_date =dayOfMonth+(mMonth+1)*100+ mYear*10000;
                }//onDateSet
            },year,month,day);
            dpd_dep.show();
        }//if v == dep_date_txt
        if(v == ret_date_txt){
            cal_ret = Calendar.getInstance();
            int day   = cal_ret.get(Calendar.DAY_OF_MONTH);
            int month = cal_ret.get(Calendar.MONTH);
            int year  = cal_ret.get(Calendar.YEAR);
            dpd_ret=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int mYear, int mMonth, int dayOfMonth)
                {
                    String return_date=dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear;
                    ret_date_txt.setText(return_date);
                    ret_date =dayOfMonth+(mMonth+1)*100+ mYear*10000;
                }//onDateSet
            },year,month,day);
            dpd_ret.show();
        }//if v == ret_date_txt
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


    public interface EditPostsListener{
        void ChangeLocation(String location, String id);
        void ChangeReturnDate(int return_date, String id);
        void ChangeDepartureDate(int departure_date, String id);

        void ChangeGender(int newGender);
        void ChangeAges(int StartAge, int EndAge);
    }
}
