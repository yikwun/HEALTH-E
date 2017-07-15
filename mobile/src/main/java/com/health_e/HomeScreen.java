package com.health_e;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.Calendar;

public class HomeScreen extends AppCompatActivity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
    private static final int MY_PERMISSIONS_REQUEST_CALLPHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_SMS = 0;

    LocationManager locationManager;
    FusedLocationProviderClient location;
    String message = "";
    double testData;
    GoogleApiClient googleApiClient;
    Model appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Log.i("HomeScreen", "OnCreate");
        appData = Model.getInstance(getApplicationContext());

        // First time the app is loaded
        if (appData.getName() == "def_name") {
            final EditText nameInput = new EditText(this);
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

            final EditText ageInput = new EditText(this);
            ageInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            ageInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

            final EditText contactInput = new EditText(this);
            contactInput.setInputType(InputType.TYPE_CLASS_TEXT);

            final EditText contactNumInput = new EditText(this);
            contactNumInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            contactNumInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

            final AlertDialog warn = new AlertDialog.Builder(this)
                    .setTitle("WARNING")
                    .setMessage("The field is empty!")
                    .setPositiveButton("Okay", null)
                    .setCancelable(false)
                    .create();

            final AlertDialog name = new AlertDialog.Builder(this)
                    .setMessage("What is your name?")
                    .setTitle("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView(nameInput)
                    .setCancelable(false)
                    .create();

            final AlertDialog age = new AlertDialog.Builder(this)
                    .setMessage("How old are you?")
                    .setTitle("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView(ageInput)
                    .setCancelable(false)
                    .create();

            final AlertDialog contact = new AlertDialog.Builder(this)
                    .setMessage("Emergency Contact Name")
                    .setTitle("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView(contactInput)
                    .setCancelable(false)
                    .create();

            final AlertDialog contactNum = new AlertDialog.Builder(this)
                    .setMessage("Emergency Contact Number")
                    .setTitle("Welcome to HEALTH-E")
                    .setPositiveButton("Next", null)
                    .setView(contactNumInput)
                    .setCancelable(false)
                    .create();

            contactNum.show();
            contact.show();
            age.show();
            name.show();

            name.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = nameInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setName(s);
                        name.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });

            age.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = ageInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setAge(s);
                        age.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });

            contact.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = contactInput.getText().toString();
                    if (s.length() > 0) {
                        appData.setEmerName(s);
                        contact.dismiss();
                    } else {
                        warn.show();
                    }
                }
            });

            contactNum.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();


        /*****************************/
        // TODO: Currently working on this
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(30 * 1000);
//        locationRequest.setFastestInterval(5 * 1000);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(HomeScreen.this, 2);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                }
//            }
//        });

        if (PermissionChecker.checkSelfPermission(HomeScreen.this, "android.permission.ACCESS_FINE_LOCATION") == PermissionChecker.PERMISSION_GRANTED) {
            location.getLastLocation()
                    .addOnSuccessListener(HomeScreen.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location l) {
//                            TextView t = (TextView) findViewById(R.id.textView);
                            if (l != null) {
                                appData.setLocation(l.getLatitude(), l.getLongitude());
//                                t.setText(appData.getLocation(HomeScreen.this));

                                TextView loc1 = (TextView) findViewById(R.id.location);
                                String m = "Your location: \n" + appData.getLocation (HomeScreen.this);
                                loc1.setText (m);

                                // location found
                                // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
                            } else {
                                // location not found
//                                t.setText("location unavailable");
                            }
                        }
                    });
        } else {
            // Permissions missing
            ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    TextView loc = (TextView) findViewById(R.id.location);
                    appData.setLocation(location.getLatitude(), location.getLongitude());
                    String message = "Your location: \n" + appData.getLocation(HomeScreen.this);
                    loc.setText(message);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                AlertDialog dialog = new AlertDialog.Builder(HomeScreen.this)
                        .setMessage("To continue, please turn on location services.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("cancel", null)
                        .create();
                dialog.show();
            }
        });
        /*****************************/

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

//        Button temp = (Button) findViewById(R.id.temp);
//        temp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PermissionChecker.checkSelfPermission(HomeScreen.this, "android.permission.ACCESS_FINE_LOCATION") == PermissionChecker.PERMISSION_GRANTED) {
//                    location.getLastLocation()
//                            .addOnSuccessListener(HomeScreen.this, new OnSuccessListener<Location>() {
//                                @Override
//                                public void onSuccess(Location l) {
//                                    TextView t = (TextView) findViewById(R.id.textView);
//                                    if (l != null) {
//                                        appData.setLocation(l.getLatitude(), l.getLongitude());
//                                        t.setText(appData.getLocation(HomeScreen.this));
//
//                                        // location found
//                                        // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
//                                    } else {
//                                        // location not found
//                                        t.setText("location unavailable");
//                                    }
//                                }
//                            });
//                } else {
//                    // Permissions missing
//                    ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//                }
//            }
////            AlertDialog popup;
////            CountDownTimer time;
////
////            @Override
////            public void onClick(View v) {
////                popup = new AlertDialog.Builder(HomeScreen.this)
////                        .setTitle("FALL DETECTED!")
////                        .setCancelable(false)
////                        .setMessage ("")
////                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                time.cancel();
////                                makeEmergencyCall();
////                            }
////                        })
////                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                time.cancel();
////                            }
////                        }).show();
////
////                time = new CountDownTimer(6000, 1000) {
////                    @Override
////                    public void onTick(long millisUntilFinished) {
////                        popup.setMessage("Would you like to contact emergency personnel?\n" +
////                                (int) millisUntilFinished / 1000 + " seconds remaining");
////                    }
////
////                    @Override
////                    public void onFinish() {
////                        popup.cancel();
////                        makeEmergencyCall();
////                    }
////                }.start();
////            }
//        });
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

    protected void sendSMSMessage() {
        Calendar c = Calendar.getInstance();
        int am = c.get(Calendar.AM_PM);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        String AM = ((am == Calendar.AM) ? "AM" : "PM");
        String HOUR = ((hour == 0) ? "12" : Integer.toString(hour));
        String MINUTE = ((minute < 10) ? "0".concat(Integer.toString(minute)) : Integer.toString(minute));

        String phoneNo = appData.getEmerNum();
        String message = appData.getName() + ", age " + appData.getAge() + ", has saved their daily information "
                + appData.getLocation(this) + " at " + HOUR + ":" + MINUTE + " " + AM + ". Temperature: "
                + appData.getTemp() + ", blood pressure: " + appData.getBP();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALLPHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + appData.getEmerNum()));
                    if (PermissionChecker.checkSelfPermission(HomeScreen.this, "android.permission.CALL_PHONE") ==
                            PermissionChecker.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                    }
                }
                break;

            case MY_PERMISSIONS_REQUEST_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(appData.getEmerNum(), null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("HomeScreen", "onPause");
        appData.savetoFile(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("HomeScreen", "OnStart");
        googleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("HomeScreen", "onDestroy");
    }

    @Override
    public void onStop() {
        Log.i("HomeScreen", "OnStop");
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

        if (message.equals("heart") && !Double.isNaN(testData)) {
//            TextView t = (TextView) findViewById(R.id.textView);
//            t.setText(Double.toString(testData));
        } else if (message.equals("call")) {
            makeEmergencyCall();
        } else if (message.equals("fall")) {

        } else if (message.equals("attack")) {

        }
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
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
