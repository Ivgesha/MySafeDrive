package com.example.mysavedrive;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import util.JournalApi;

public class MainActivity extends AppCompatActivity {


    private ImageView logoImage;
    private TextView title;
    private TextView gratefulText;
    private TextView about;
    private Button getStartedButton;
private RelativeLayout relativeLayout;

    //Checking if the user login
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");//In order to check if the user is login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    logoImage=findViewById(R.id.logoIcon);
        title = findViewById(R.id.title);
        gratefulText = findViewById(R.id.gratefulText);
        about = findViewById(R.id.about);
        getStartedButton = findViewById(R.id.startButton);
        relativeLayout = findViewById(R.id.relativeLayout);

        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottomtotop);

        logoImage.startAnimation(animation);
        title.startAnimation(animation);
      //  gratefulText.startAnimation(animation);
        about.startAnimation(animation);
        getStartedButton.startAnimation(animation);
        relativeLayout.startAnimation(animation);


        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    currentUser = firebaseAuth.getCurrentUser();
                    final String currentUserId = currentUser.getUid();

                    collectionReference.whereEqualTo("userId", currentUserId)//Search the user in the firebase
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null)//Something is wrong and we are getting exception
                                    {
                                        return;
                                    }

                                    String name;
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUserId(snapshot.getString("userId"));
                                            journalApi.setUsername(snapshot.getString("username"));

                                            startActivity(new Intent(MainActivity.this, JournalListActivity.class));
                                            finish();//In order to cancel the potion to comeback to the mainActivity
                                        }
                                    }

                                }
                            });

                } else {


                }

            }
        };


        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //from here we are going to login activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Let us to pass from the welcome page to the login/signUp page
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
