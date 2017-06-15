package com.health_e;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class HomeScreen extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALLPHONE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

//        Model model = new Model();

        Button settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, SettingsActivity.class));
            }
        });

        Button input = (Button) findViewById(R.id.input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, Input.class));
            }
        });


        //Emergency Call function:
        //

        Button call = (Button) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEmergencyCall();
            }
        });

        Button temp = (Button) findViewById(R.id.temp);
        temp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
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
    }

    protected void makeEmergencyCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:2267917318"));
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

        private class PhoneCallListener extends PhoneStateListener {
            private boolean isPhoneCalling = false;
            String LOG_TAG = "LOGGING 123";
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                if (TelephonyManager.CALL_STATE_RINGING == state) {
                    // phone ringing
                    Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
                }

                if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                    // active
                    Log.i(LOG_TAG, "OFFHOOK");

                    isPhoneCalling = true;
                }

                if (TelephonyManager.CALL_STATE_IDLE == state) {
                    // run when class initial and phone call ended,
                    // need detect flag from CALL_STATE_OFFHOOK
                    Log.i(LOG_TAG, "IDLE");

                    if (isPhoneCalling) {

                        Log.i(LOG_TAG, "restart app");

                        // restart app
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(
                                        getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        isPhoneCalling = false;
                    }

                }
            }
        }

    }
