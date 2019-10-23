package com.example.mysavedrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.Journal;
import ui.JournalRecyclerAdapter;
import util.JournalApi;

public class JournalListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Journal");
    private TextView noJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        firebaseAuth =  FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noJournalEntry= findViewById(R.id.listNodrive);//the name was list_no_thoughts

        journalList = new ArrayList<>();
        recyclerView =findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:
                //Take user to add new Drive
                if(user != null && firebaseAuth != null)
                {
                    startActivity(new Intent(JournalListActivity.this, PostJournalActivity.class));
                    //finish();
                }

                break;
            case R.id.action_signout:
                //signout the user
                if(user != null && firebaseAuth != null)
                {
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                    //finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //Query our storage (fireBase) in order to get the date
        collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                //We are using the api that we created
                // in order to get the info from the database (fireBase) for this user

        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                if(!queryDocumentSnapshots.isEmpty())//check if our db is not empty
                {
                    for (QueryDocumentSnapshot journals : queryDocumentSnapshots)//We are searching the user which is login
                    {
                        Journal journal = journals.toObject(Journal.class);
                        journalList.add(journal);
                    }
                    //Invoke recyclerView
                    journalRecyclerAdapter = new JournalRecyclerAdapter(JournalListActivity.this, journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();//The system now to update herself if something changed
                }
                else
                {
                    noJournalEntry.setVisibility(View.VISIBLE);//If the user is login for the first time then show "no events added yet"

                }

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
    }
}
