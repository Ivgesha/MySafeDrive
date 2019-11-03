package com.example.mysavedrive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class LoginActivity extends AppCompatActivity {


    private ImageView logoIcon;
    private TextView emailTextView;
    private TextView passwordTextView;
    private Button signUpButton;
    private TextView orTextView;
    private Button createAccountButton;

    private Button loginButton;
    private Button createAcctButton;

    private AutoCompleteTextView emailAddres;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth currentUser;

    private ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bottomtotop);

        logoIcon = findViewById(R.id.logoIcon);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        signUpButton = findViewById(R.id.email_sign_in_button);
        orTextView = findViewById(R.id.orTextView);
        createAccountButton = findViewById(R.id.create_acct_button_login);

        logoIcon.startAnimation(animation);
        emailTextView.startAnimation(animation);
        passwordTextView.startAnimation(animation);
        signUpButton.startAnimation(animation);
        orTextView.startAnimation(animation);
        createAccountButton.startAnimation(animation);

        progressBar = findViewById(R.id.login_progress);

        firebaseAuth = firebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        emailAddres = findViewById(R.id.email);
        password = findViewById(R.id.password);


        loginButton = findViewById(R.id.email_sign_in_button);
        createAcctButton = findViewById(R.id.create_acct_button_login);


        // create account Button
        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });

        // LogIn Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginEmailPasswordUser(emailAddres.getText().toString().trim(), password.getText().toString().trim());

            }
        });
    }

    private void loginEmailPasswordUser(String email, String pwd)//pws = password
    {
        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    if (user == null) {
                        return;
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    String currentUserId = user.getUid();

                    //Invoke the collection from the firebase loop all over the users to find the correct user
                    collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                            }
                            assert queryDocumentSnapshots != null;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    JournalApi journalApi = JournalApi.getInstance();
                                    journalApi.setUsername(snapshot.getString("username"));
                                    journalApi.setUserId(snapshot.getString("userId"));

                                    //Go to ListActivity

                                    startActivity(new Intent(LoginActivity.this, PostJournalActivity.class));

                                }
                            }

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Wrong User OR Password ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_LONG).show();
        }
    }
}
