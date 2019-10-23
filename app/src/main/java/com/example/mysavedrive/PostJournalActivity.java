package com.example.mysavedrive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import model.Journal;
import util.JournalApi;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = '1';//delte
    private static final String TAG = "PostJournalActivity";
    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;//delete after
    private EditText titleEditText;//delete after
    private EditText thoughtsEditText;//delete after
    private TextView currentUserTextView;
    private ImageView imageView;
    private Button signalButton;


    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");//another collection of journals
    private Uri imageUri;//delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        Animation animation = AnimationUtils.loadAnimation(PostJournalActivity.this, R.anim.lefttoright);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = firebaseAuth.getInstance();
        progressBar = findViewById(R.id.post_progressBar);
        titleEditText = findViewById(R.id.post_title_et);//delete
        thoughtsEditText = findViewById(R.id.post_description_et);//delete
        currentUserTextView = findViewById(R.id.post_username_textview);
        signalButton = findViewById(R.id.show_map_button);

   //    titleEditText.startAnimation(animation);
      //    thoughtsEditText.startAnimation(animation);
   //    currentUserTextView.startAnimation(animation);
 //      signalButton.startAnimation(animation);
//        imageView.startAnimation(animation);
//       saveButton.startAnimation(animation);
//
        signalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostJournalActivity.this, ShowUserLocaion.class));

            }
        });


        imageView = findViewById(R.id.post_imageView);//delete

        saveButton = findViewById(R.id.post_save_journal_button);
        saveButton.setOnClickListener(this);
        addPhotoButton = findViewById(R.id.postCameraButton);//delte after
        addPhotoButton.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);//Dont display the progress bar

        if (JournalApi.getInstance() != null)//Display the user name and the id on the home page
        {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUsername();

            currentUserTextView.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }

            }
        };


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_save_journal_button:
                //save jurnal
                saveJournal();
                break;
            case R.id.postCameraButton://deltet after
                //getImageFromgallery/phone
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");//anthing that is image related will be workoing
                startActivityForResult(galleryIntent, GALLERY_CODE);//Asking to get something from the os

                break;

            case R.id.show_map_button:
                //This button show the map and ths location of the user and also the user


                break;
        }

    }

    private void saveJournal() {
        final String title = titleEditText.getText().toString().trim();
        final String thoughts = thoughtsEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {
            final StorageReference filepath = storageReference.child("journal_images")
                    .child("my_image_" + Timestamp.now().getSeconds());//we are creatinh a foldor that will save the images that we add
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                //on the moment we have our image we are going to do all of our work
                {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            //Todo: create a journal object - model
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThought(thoughts);
                            journal.setImageUrl(imageUrl);
                            //journal.setTimeAdded(new Timestamp(new Date()));//The moment that this item was added to our fire store db
                            journal.setUserName(currentUserName);
                            journal.setUserId(currentUserId);

                            //Todo: invok our collectionRference and adding the new itrm to iuet collection
                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostJournalActivity.this, JournalListActivity.class));
                                    finish();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());

                                        }
                                    });

                            //Todo: and save a journal instance
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)//delte after
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null)//the data refer to the data that we are getting from the INTENT
            {
                imageUri = data.getData();//the path to the image we are trying to get
                imageView.setImageURI(imageUri);//show image


            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null)//In order to stop listening to the DB  after the app is stops working
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
