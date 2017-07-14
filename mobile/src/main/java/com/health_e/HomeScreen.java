package com.health_e;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.PhoneStateListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;

public class HomeScreen extends AppCompatActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
    private static final int MY_PERMISSIONS_REQUEST_CALLPHONE = 1;
    private FusedLocationProviderClient location;
    String message = "";
    double testData;
    GoogleApiClient googleApiClient;
    Model appData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.i("HomeScreen","OnCreate");
        appData = Model.getInstance(getApplicationContext());

        // First time the app is loaded
        if (appData.getName() == "def_name") {
            final EditText nameInput = new EditText (this);
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

            final EditText ageInput = new EditText (this);
            ageInput.setInputType (InputType.TYPE_CLASS_NUMBER);
            ageInput.setFilters (new InputFilter[] { new InputFilter.LengthFilter (3)});

            final EditText contactInput = new EditText (this);
            contactInput.setInputType (InputType.TYPE_CLASS_TEXT);

            final EditText contactNumInput = new EditText (this);
            contactNumInput.setInputType (InputType.TYPE_CLASS_NUMBER);
            contactNumInput.setFilters (new InputFilter[] { new InputFilter.LengthFilter (11)});

            final AlertDialog warn = new AlertDialog.Builder(this)
                    .setTitle ("WARNING")
                    .setMessage ("The field is empty!")
                    .setPositiveButton("Okay", null)
                    .create();

            final AlertDialog name = new AlertDialog.Builder(this)
                    .setMessage ("What is your name?")
                    .setTitle ("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView (nameInput)
                    .create();

            final AlertDialog age = new AlertDialog.Builder (this)
                    .setMessage ("How old are you?")
                    .setTitle ("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView (ageInput)
                    .create();

            final AlertDialog contact = new AlertDialog.Builder (this)
                    .setMessage ("Emergency Contact Name")
                    .setTitle ("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView (contactInput)
                    .create();

            final AlertDialog contactNum = new AlertDialog.Builder (this)
                    .setMessage ("Emergency Contact Number")
                    .setTitle ("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView (contactNumInput)
                    .create();

            contactNum.show();
            contact.show();
            age.show();
            name.show();

            name.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String s = nameInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setName(s);
                        name.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });

            age.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String s = ageInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setAge(s);
                        age.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });

            contact.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String s = contactInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setEmerName(s);
                        contact.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });

            contactNum.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String s = contactNumInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setEmerNum(s);
                        contactNum.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });
        }

        location = LocationServices.getFusedLocationProviderClient(this);
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


        Button call = (Button) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEmergencyCall();
            }
        });

        Button temp = (Button) findViewById(R.id.temp);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (PermissionChecker.checkSelfPermission(HomeScreen.this, "android.permission.ACCESS_FINE_LOCATION") == PermissionChecker.PERMISSION_GRANTED) {
                    location.getLastLocation()
                            .addOnSuccessListener(HomeScreen.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location l) {
                                    if (l != null) {
                                        appData.setLocation (l.getLatitude(), l.getLongitude());
                                        TextView t = (TextView) findViewById(R.id.textView);
                                        t.setText (appData.getLocation());

                                        // location found
                                        // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
                                    } else {

                                        // location not found
                                    }
                                }
                            });
                } else {

                    // Permissions missing
                    ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            }
//            AlertDialog popup;
//            CountDownTimer time;
//
//            @Override
//            public void onClick(View v) {
//                popup = new AlertDialog.Builder(HomeScreen.this)
//                        .setTitle("FALL DETECTED!")
//                        .setCancelable(false)
//                        .setMessage ("")
//                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                time.cancel();
//                                makeEmergencyCall();
//                            }
//                        })
//                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                time.cancel();
//                            }
//                        }).show();
//
//                time = new CountDownTimer(6000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        popup.setMessage("Would you like to contact emergency personnel?\n" +
//                                (int) millisUntilFinished / 1000 + " seconds remaining");
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        popup.cancel();
//                        makeEmergencyCall();
//                    }
//                }.start();
//            }
        });
    }

    protected void makeEmergencyCall() {
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + appData.getEmerNum()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALLPHONE);
        } else {
            startActivity(callIntent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALLPHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + appData.getEmerNum()));
                    if (PermissionChecker.checkSelfPermission(HomeScreen.this, "android.permission.CALL_PHONE") ==
                            PermissionChecker.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    protected  void onPause(){
        super.onPause();
        Log.i("HomeScreen","onPause");
        appData.savetoFile(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("HomeScreen","OnStart");
        googleApiClient.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("HomeScreen","onDestroy");
    }

    @Override
    public void onStop() {
        Log.i("HomeScreen","OnStop");
        if (null != googleApiClient && googleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(googleApiClient, this);
            googleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(googleApiClient, this);
        Toast.makeText(getApplicationContext(), "Connected to Google API Client", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(getApplicationContext(), "Suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        message = messageEvent.getPath();
        testData = toDouble(messageEvent.getData());
        if (message != null && !Double.isNaN(testData)) {
            makeEmergencyCall();
//            Toast.makeText(getApplicationContext(), "Message is: " + message, Toast.LENGTH_LONG).show();show
//            Toast.makeText(getApplicationContext(), "Data is: " + String.valueOf(testData), Toast.LENGTH_LONG).show();
        }
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public void showText() {
        Toast.makeText(getApplicationContext(), "Message is: " + message, Toast.LENGTH_LONG).show();
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
