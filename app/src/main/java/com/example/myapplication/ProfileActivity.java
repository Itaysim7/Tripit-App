package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener, EditPostsDialog.EditPostsListener, EditProfileDialog.EditProfileListener {

    //variables for the photo upload
    private StorageReference storageReference;
    private static final String TAG = "ProfileActivity";
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;


    private DatabaseReference reference;
    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView mFirestoreList;
    private FirestorePagingAdapter<PostsModel, PostsViewHolder> adapter;
    private UsersObj user;
    private Query query;

    private ImageView image_profile;
    private TextView about_myself_txt;
    private TextView myPosts;
    private TextView name_age_txt;
    private Button edit_profile_btn;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        image_profile = findViewById(R.id.image_profile_view);
        about_myself_txt = findViewById(R.id.About_Myself_Txt);
        myPosts = findViewById(R.id.My_Posts);
        name_age_txt = findViewById(R.id.name_and_age_txt);
        edit_profile_btn = findViewById(R.id.Edit_profile_btn);
        mScrollView = findViewById(R.id.scrollView);

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ScrollPositionObserver());

        //firebase
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        mFirestoreList=findViewById(R.id.firestore_list);

        db = FirebaseFirestore.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("users").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UsersObj.class);
                //Set name
                String intro = user.getFullName();
                if(user.getAge() != 0){
                    intro = intro + ", " + user.getAge();
                }
                name_age_txt.setText(intro);

                //set image
                if(user != null && user.getImageUrl().equals("default")) {
                    image_profile.setImageResource(R.drawable.user_image);
                } else{
                    Glide.with(ProfileActivity.this).load(user.getImageUrl()).into(image_profile);
                }
                //set description
                if(user.getDescription().equals("empty")) {
                    about_myself_txt.setText("למשתמש זה אין שום דבר לומר על עצמו.");
                } else{
                    about_myself_txt.setText(user.getDescription());
                }
                //find the posts this user uploaded
                query = db.collection("Posts").whereEqualTo("user_id", fUser.getUid()).whereEqualTo("approval",true).orderBy("timestamp",Query.Direction.DESCENDING).limit(100);
                //Check if the user uploaded posts. If not, change the text.
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        if(value.isEmpty())
                            myPosts.setText("לא קיימים פוסטים להצגה.");
                    }
                });
                PagedList.Config config = new PagedList.Config.Builder().setInitialLoadSizeHint(8).setPageSize(2).build();

                //recyclerOptions
                FirestorePagingOptions<PostsModel> options = new FirestorePagingOptions.Builder<PostsModel>()
                        .setQuery(query, config, PostsModel.class).build();
                adapter = new FirestorePagingAdapter<PostsModel, PostsViewHolder>(options) {
                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_in_profile, parent, false);
                        return new PostsViewHolder(view);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull PostsModel model) { //set data
                        holder.list_departure_date.setText("תאריך יציאה: " + model.getDeparture_date());
                        holder.list_return_date.setText("תאריך חזרה: " + model.getReturn_date());
                        holder.list_destination.setText("יעד: " + model.getDestination());
                        holder.list_age.setText("טווח גילאים: " + model.getAge());
                        holder.list_gender.setText("מין: " + model.getGender());
                        holder.list_description.setText("תיאור: " + model.getDescription());
                        holder.list_type.setText("מטרות הטיול שלי: " + model.getType_trip());

                        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openDialog(model);
                            }
                        });
                        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.collection("Posts").document(model.getId()).delete();
                            }
                        });
                    }

                    public void openDialog(PostsModel model){
                        EditPostsDialog dialog = new EditPostsDialog(model);
                        dialog.show(getSupportFragmentManager(), "Edit Post");
                    }
                };

                mFirestoreList.setHasFixedSize(true);
                mFirestoreList.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                mFirestoreList.setAdapter(adapter);
                adapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed", error.getMessage());
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        image_profile.setOnClickListener(this);
        edit_profile_btn.setOnClickListener(this);


        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false); //delete the default title

    }


    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        if (v == image_profile){
            openImage();
        }

        if(v == edit_profile_btn){
            openEditProfileDialog();
        }
//        if(v == about_myself_txt){
//            changeText();
//        }
    }

    private void openEditProfileDialog() {
        EditProfileDialog dialog = new EditProfileDialog(user);
        dialog.show(getSupportFragmentManager(), "Edit Profile");
    }

    //-----------------------------Change Description Function-------------------------
    private void changeText() {
        String newDescription = about_myself_txt.getText().toString();
        user.setDescription(newDescription);
        reference.setValue(user);
    }

    //-----------------------------Upload Image Function ------------------------------
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
            try {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) throw task.getException();
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();

                            fUser = mAuth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageUrl", mUri);
                            reference.updateChildren(map);

                            pd.dismiss();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
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


    //---------------------------Methods for the posts dialog-------------------------
    @Override
    public void ChangeLocation(String location, String id) {
        db.collection("Posts").document(id).update("destination",location);
    }

    @Override
    public void ChangeReturnDate(int return_date, String id) {
        db.collection("Posts").document(id).update("return_date",return_date);
    }

    @Override
    public void ChangeDepartureDate(int departure_date, String id) {
        db.collection("Posts").document(id).update("departure_date",departure_date);
    }

    @Override
    public void ChangeGender(String newGender, String id) {
        db.collection("Posts").document(id).update("gender", newGender);
    }

    @Override
    public void ChangeAges(String StartAge, String EndAge, String id) {
        String range = StartAge+"-"+EndAge;
        db.collection("Posts").document(id).update("age",range);
    }

    @Override
    public void ChangeDescription(String Description, String id) {
        if(Description.equals(""))
            Description = "ספר על עצמך";
        db.collection("Posts").document(id).update("description", Description);
    }

    @Override
    public void ChangeTripType(ArrayList<String> trip_types, String id) {
        db.collection("Posts").document(id).update("type_trip", trip_types);
    }

    //----------------------------------Methods for the edit profile dialog---------------------

    @Override
    public void ChangeName(String newName) {
        reference.child("fullName").setValue(newName);
    }

    @Override
    public void ChangeGenderForProfile(String newGender) {
        reference.child("gender").setValue(newGender);
    }

    @Override
    public void ChangeProfileDescription(String newDescription) {
        reference.child("description").setValue(newDescription);
    }

    @Override
    public void ChangeBirthdayDate(int date) {
        reference.child("Birthday").setValue(date);
    }


    //------------------------Posts Class---------------------------------------
    private class PostsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_departure_date;
        private TextView list_return_date;
        private TextView list_destination;
        private TextView list_age;
        private TextView list_gender;
        private TextView list_description;
        private TextView list_type;

        private Button edit_btn, delete_btn;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_departure_date=itemView.findViewById(R.id.list_departure_date);
            list_return_date=itemView.findViewById(R.id.list_return_date);
            list_destination=itemView.findViewById(R.id.list_destination);
            list_age=itemView.findViewById(R.id.list_age);
            list_gender=itemView.findViewById(R.id.list_gender);
            list_description=itemView.findViewById(R.id.list_description);
            list_type=itemView.findViewById(R.id.list_type);

            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }


    //------------------------Class Responsible for Changes when Scrolling----------------------
    private class ScrollPositionObserver implements ViewTreeObserver.OnScrollChangedListener {
        private final int mImageViewHeight;

        public ScrollPositionObserver() {
            mImageViewHeight = getResources().getDimensionPixelSize(R.dimen.contact_photo_height);
        }

        @Override
        public void onScrollChanged() {
            int baseColor = getColor(R.color.primary);
            int scrollY = Math.min(Math.max(mScrollView.getScrollY(), 0), mImageViewHeight);
            float alpha = scrollY / (float) mImageViewHeight;

            // changing position of ImageView
            //image_profile.setTranslationY((float) (scrollY / 8));
            image_profile.setColorFilter(getColorWithAlpha(alpha, baseColor));

            //Don't let name disappear -
            int distance_txt = name_age_txt.getTop() - mScrollView.getScrollY();
            if(distance_txt < 0 )
                name_age_txt.offsetTopAndBottom(10);


        }

        public int getColorWithAlpha(float alpha, int baseColor) {
            int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
            int rgb = 0x00ffffff & baseColor;
            return a + rgb;
        }
    }


}
