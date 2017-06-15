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
import android.support.annotation.Nullable;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;

public class HomeScreen extends AppCompatActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks{
    private static final int MY_PERMISSIONS_REQUEST_CALLPHONE = 1;
    String message="";
    double testData;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

//        Model model = new Model();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();



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
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:2267917318"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALLPHONE);
        } else {
            startActivity(callIntent);
        }
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALLPHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:2267917318"));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop(){

        if (null != googleApiClient && googleApiClient.isConnected()){
            Wearable.MessageApi.removeListener(googleApiClient, this);
            googleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(googleApiClient,this);
        Toast.makeText(getApplicationContext(),"Connected to Google API Client",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(getApplicationContext(),"Suspended",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        message = messageEvent.getPath();
        testData = toDouble(messageEvent.getData());
        if (message != null && !Double.isNaN(testData)) {
            Toast.makeText(getApplicationContext(), "Message is: " + message, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"Data is: " + String.valueOf(testData),Toast.LENGTH_LONG).show();
        }
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public void showText()
    {
        Toast.makeText(getApplicationContext(),"Message is: " + message,Toast.LENGTH_LONG).show();
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
