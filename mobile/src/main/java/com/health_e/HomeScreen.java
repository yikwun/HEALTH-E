package com.health_e;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

//        Model model = new Model();

        Button settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity (new Intent(HomeScreen.this, SettingsActivity.class));
            }
        });

        Button input = (Button) findViewById(R.id.input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity (new Intent (HomeScreen.this, Input.class));
            }
        });

        Button call = (Button) findViewById(R.id.call);
        call.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Emergency call function
            }
        });

        Button temp = (Button) findViewById(R.id.temp);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HomeScreen.this)
                        .setTitle("FALL DETECTED!")
                        .setMessage("Would you like to contact emergency personnel?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Call emergency contact
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();
            }
        });

        // Track heart rate here??
        // https://material.io/icons/
    }
}
