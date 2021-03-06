package com.gardenvillagers.chitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    void updateUI(FirebaseUser thisUser){

        if(thisUser == null) {
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        }
        else {
            Toast.makeText(this,"Hello "+ thisUser.getEmail(),
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, DisplayCost.class);
            startActivity(i);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}
