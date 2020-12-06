package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //variables for the photo upload
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private DatabaseReference reference;
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private UsersObj user;

    private ImageView image_profile;
    private EditText about_myself_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();


        image_profile = findViewById(R.id.image_profile_view);
        about_myself_txt = findViewById(R.id.About_Myself_Txt);


        reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
                //set image
                if(user.getImageUrl().equals("default")) {
                    image_profile.setImageResource(R.drawable.user_image);
                }
                else{
                    Glide.with(ProfileActivity.this).load(user.getImageUrl()).into(image_profile);
                }
                //set description
                if(user.getDescription().equals("empty")) {
                    about_myself_txt.setText("למשתמש זה אין שום דבר לומר על עצמו.");
                }
                else{
                    about_myself_txt.setText(user.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", error.getMessage());
            }
        });


        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        image_profile.setOnClickListener(this);
        about_myself_txt.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

    }

    @Override
    public void onClick(View v) {
        if (v == image_profile){
            openImage();
        }
        if(v == about_myself_txt){
            changeText();
        }
    }

    private void changeText() {
        String newDescription = about_myself_txt.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("description",newDescription);
        reference.updateChildren(map);



    }


    /**
    * Allows the user to select an image from his phone
     */
    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);

        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) throw task.getException();
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        fUser = mAuth.getCurrentUser();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUrl",mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }
                    else{
                        Toast.makeText(ProfileActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else{
            Toast.makeText(ProfileActivity.this, "No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null || data.getData() == null){
            Toast.makeText(ProfileActivity.this, "Opening image Failed.",Toast.LENGTH_SHORT).show();
        }

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(ProfileActivity.this, "Uploading In Progress",Toast.LENGTH_SHORT).show();
            }
            else{
                uploadImage();
            }
        }
    }


    //------------------------Toolbar functions-------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_munu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newPost:
                return true;

            case R.id.Search:

            case R.id.myProfile:
                Intent toProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(toProfile);

            case R.id.savePost:

            case R.id.logOut:
                mAuth.signOut();
                finish();
                Intent intent = new Intent(getApplicationContext(), welcomeActivity.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}