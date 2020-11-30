package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CreatePost extends AppCompatActivity implements View.OnClickListener
{
    private static final int MAX_LENGTH=100; //max length of random string
    private String age_text = "",departure_date="",return_date="",gender="",current_user_id="";
    private ImageView new_post_image;
    private EditText destination,description;
    private Uri post_image_uri;
    private StorageReference storage_reference;
    private FirebaseFirestore firebase_firestore;
    private FirebaseAuth firebaseAuth;
    TextView text_dep_date,text_ret_date,text_type_trip;
    Button btn_dep_date,btn_ret_date,btn_type_trip,btn_gender,btn_age,btn_publish;
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

        //firebase
        storage_reference= FirebaseStorage.getInstance().getReference();
        firebase_firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        current_user_id=firebaseAuth.getCurrentUser().getUid();

        //image
        new_post_image=findViewById(R.id.add_photo);
        //EditText
        description=findViewById(R.id.description);
        destination=findViewById(R.id.destination);
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
        //Listener for image
        new_post_image.setOnClickListener(this);

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
                    departure_date=dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear;
                    text_dep_date.setText(departure_date);
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
                    return_date=dayOfMonth+ "/" + (mMonth+1) + "/"+ mYear;
                    text_ret_date.setText(return_date);
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
        else if(view==new_post_image)
        {
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(512,512)
                    .setAspectRatio(1,1)
                    .start(CreatePost.this);
        }
        else if(view==btn_publish)
        {
            String dest=destination.getText().toString();
            String desc=description.getText().toString();
            if(!TextUtils.isEmpty(desc)&&post_image_uri!=null)
            {
                String random_name= random();
                StorageReference file_path=storage_reference.child("post_images").child(random_name+".jpg");
                System.out.println("mess1");
                file_path.putFile(post_image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            String download_uri=task.getResult().getStorage().getDownloadUrl().toString();
                            Map<String,Object> post_map=new HashMap<>();

                            post_map.put("approval",0);
                            post_map.put("user_id",current_user_id);
                            post_map.put("timestamp",FieldValue.serverTimestamp());
                            post_map.put("destination",dest);
                            post_map.put("departure_date",departure_date);
                            post_map.put("return_date",return_date);
                            post_map.put("age",age_text);
                            post_map.put("gender",gender);
                            post_map.put("type_trip",text_type_trip.toString());
                            post_map.put("image_url",download_uri);
                            post_map.put("description",desc);
                            post_map.put("clicks",0);

                            firebase_firestore.collection("Posts").add(post_map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(),"הפוסט נשלח לאישור מנהל",Toast.LENGTH_LONG).show();
                                        Intent home=new Intent(CreatePost.this,homePage.class);
                                        startActivity(home);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"פרסום הפוסט נכשל",Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                }
                            });
                        }
                        else
                        {

                        }

                    }
                });
            }
            else
            {
                Toast.makeText(getApplicationContext(),"אתה צריך להוסיף תמונה ולספר על עצמך",Toast.LENGTH_LONG).show();
                return;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                    post_image_uri = result.getUri();
                    new_post_image.setImageURI(post_image_uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    /*
        The function generator a random string
     */
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


}


