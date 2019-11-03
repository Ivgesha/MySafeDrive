package com.example.mysavedrive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {
    private int test =0;
    private Button loginButton;
    private Button createAcctButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener; //Listen to the events from the firebase
    private FirebaseUser currentUser;//Patch the user which want to be login

    //FireStore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();//the DB for the user Account

    private CollectionReference collectionReference = db.collection("Users");

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button createAccountButton;
    private EditText userNameEditText;
    private EditText fullNameEditText;
    private EditText carModelEditText;
    private EditText plateNumberEditText;
    private EditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        firebaseAuth = FirebaseAuth.getInstance();

        createAccountButton = findViewById(R.id.create_acct_button);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.username_account);
        fullNameEditText = findViewById(R.id.full_name_account);
        carModelEditText = findViewById(R.id.carModel_account);
        plateNumberEditText = findViewById(R.id.plateNumber_account);
        phoneNumberEditText = findViewById(R.id.phoneNumber_account);

        Animation animation = AnimationUtils.loadAnimation(CreateAccountActivity.this, R.anim.bottomtotop);
        createAccountButton.startAnimation(animation);
        progressBar.startAnimation(animation);
        emailEditText.startAnimation(animation);
        passwordEditText.startAnimation(animation);
        userNameEditText.startAnimation(animation);
        fullNameEditText.startAnimation(animation);
        carModelEditText.startAnimation(animation);
        plateNumberEditText.startAnimation(animation);
        phoneNumberEditText.startAnimation(animation);
        authStateListener = new FirebaseAuth.AuthStateListener()// in this patr we are doing our work keep listeing t changes
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    //user is already logedin

                } else {
                    //No user yet
                }

            }
        };

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(emailEditText.getText().toString())
                        && !TextUtils.isEmpty(passwordEditText.getText().toString())
                        && !TextUtils.isEmpty(userNameEditText.getText().toString())) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = userNameEditText.getText().toString().trim();
                    String fullname = fullNameEditText.getText().toString().trim();
                    String carModel = carModelEditText.getText().toString().trim();
                    String plateNumer = plateNumberEditText.getText().toString().trim();
                    String phoneNumber = phoneNumberEditText.getText().toString().trim();

                    createUserEmailAccount(email, password, username, fullname, carModel, plateNumer, phoneNumber);
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Empty fields not allowed",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String userName,
                                        final String fullname, final String carModel, final String plateNumber, final String phoneNumber)//creating the new account
    {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(userName)
                && !TextUtils.isEmpty(fullname)
                && !TextUtils.isEmpty(carModel)
                && !TextUtils.isEmpty(plateNumber)
                && !TextUtils.isEmpty(phoneNumber))//checking if all the fildes in the signup page is full
        {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                //checking if all the progress of the creating new account pass correctly
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //we take user to the AddJournalActivity
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();

                        //Create a user map so we can create a user in the user collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userId", currentUserId);
                        userObj.put("username", userName);
                        userObj.put("fullName", fullname);
                        userObj.put("carModel", carModel);
                        userObj.put("plateNumber", plateNumber);
                        userObj.put("phoneNumber", phoneNumber);


                        //save to our firestore database
                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {//now we now that everthing is good and we have created our otentecated user object
                                        //and now we allow to add this user
                                        if (Objects.requireNonNull(task.getResult()).exists()) {//to make the progress bar go away
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = task.getResult()
                                                    .getString("username");

                                            JournalApi journalApi = JournalApi.getInstance();//Global API
                                            journalApi.setUserId(currentUserId);
                                            journalApi.setUsername(name);

                                            String full_name = task.getResult().getString("fullname");
                                            String car_model = task.getResult().getString("carModel");
                                            String plate_number = task.getResult().getString("plateNumer");
                                            String phone_number = task.getResult().getString("phoneNumber");

                                            Intent intent = new Intent(CreateAccountActivity.this,
                                                    PostJournalActivity.class);
                                            intent.putExtra("username", name);
                                            intent.putExtra("userId", currentUserId);
                                            intent.putExtra("fullname", full_name);
                                            intent.putExtra("carModel", car_model);
                                            intent.putExtra("plateNumer", plate_number);
                                            intent.putExtra("phoneNumber", phone_number);

                                            startActivity(intent);

                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);


                                        }


                                    }
                                });
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                    } else {
                        //something went wrong

                    }

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        } else {

        }
    }

    @Override
    protected void onStart() {
        //check to see if the user is currently signed in.
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser(); // getCurrentUser Method is part of the firebase
        firebaseAuth.addAuthStateListener(authStateListener);//this part will bew listening to any change that happenings
    }
}
