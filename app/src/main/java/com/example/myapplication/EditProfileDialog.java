package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import java.util.Calendar;

/**
 edit profile dialog Activity represent dialog for edit details of the user in his profile
 edit profile dialog Activity have the following functionality:
    The user can change every field that he wants.
 */
public class EditProfileDialog extends AppCompatDialogFragment implements View.OnClickListener
{
    //field in his profile
    private EditText name_edit_txt, description_edit_txt;
    private Button choose_gender_btn, choose_birthday_btn;
    private String gender;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private int birthday_int;
    //user object that will store the data of the user from db
    private UsersObj user;

    private EditProfileListener listener;

    public EditProfileDialog(UsersObj user) {
        this.user = user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        //open custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_profile_dialog, null);

        //Local Variables
        name_edit_txt = view.findViewById(R.id.name_edit_txt);
        description_edit_txt = view.findViewById(R.id.tell_about_yourself_edit_txt);
        choose_gender_btn =  view.findViewById(R.id.gender_btn);
        choose_birthday_btn = view.findViewById(R.id.birthday_btn);

        //listener for button
        choose_gender_btn.setOnClickListener(this);
        choose_birthday_btn.setOnClickListener(this);

        gender = user.getGender();
        //set field
        choose_gender_btn.setText(gender);
        name_edit_txt.setText(user.getFullName());
        description_edit_txt.setText(user.getDescription());
        if(user.getBirthday() != 0)
            choose_birthday_btn.setText(user.getBirthday_String());

        dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
                choose_birthday_btn.setText(date);
                birthday_int = dayOfMonth + (month + 1) * 100 + year * 10000;
            }
        };

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
                        String newName = name_edit_txt.getText().toString();
                        if(!newName.equals(""))
                            listener.ChangeName(newName);
                        listener.ChangeGenderForProfile(gender);

                        String newDescription = description_edit_txt.getText().toString();
                        if(newDescription.equals(""))
                            newDescription = "empty";
                        listener.ChangeProfileDescription(newDescription);
                        listener.ChangeBirthdayDate(birthday_int);
                        Refresh();

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditProfileListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement EditPostsDialog!");
        }
    }//onAttach


    @Override
    public void onClick(View v) {
        if(v == choose_birthday_btn){
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListener, year, month, day);
            dialog.show();

        }//if choose_birthday_btn
        if(v == choose_gender_btn){
            String [] list_gender=new String[]{"אישה", "גבר", "לא משנה"};
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
            mBuilder.setTitle("בחר עם איזה מין אתה מעוניין לטייל");
            mBuilder.setSingleChoiceItems(list_gender, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    gender=list_gender[which];
                    choose_gender_btn.setText(gender);
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
        } //if choose_gender_btn
    }//onClick

    /**
     * The function Refresh the page after the user edit his details.
     */
    private void Refresh(){
        Intent intent = new Intent(this.getActivity().getIntent());
        startActivity(intent);
    }

    /**
     * interface for edit profile listener, with the following function:
     * 1)ChangeName - changes the full name of the user
     * 2)ChangeGenderForProfile - changes the gender of the user
     * 3)ChangeProfileDescription - changes the description of the user
     * 4)ChangeBirthdayDate - changes the Birthday date of the user
     */
    public interface EditProfileListener{
        void ChangeName(String newName);
        void ChangeGenderForProfile(String newGender);
        void ChangeProfileDescription(String newDescription);
        void ChangeBirthdayDate(int date);
    }


}
