package com.health_e;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.R.attr.gravity;

public class MainActivity extends Activity {

    private TextView mTextView;
    public SensorManager mSensorManager;
    public Sensor mSensor;
    float gravity[];
    float linear_acceleration[];
    String nodeId;
    final int CONNECTION_TIME_OUT_MS = 5000;
    String MESSAGE = "Hello from the other world!";
    double senseArray[] = {1.2,3.4,5.6,7.8,9.1};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);
        /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });*/

        Button sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            AlertDialog popup;
            CountDownTimer time;
            Vibrator v1 = (Vibrator) getSystemService (Context.VIBRATOR_SERVICE);
            long[] pattern = {400, 400};
            @Override
            public void onClick(View v) {
                v1.vibrate (pattern, 0);

                retrieveDeviceNode();
//                Toast.makeText(getApplicationContext(),"Device Node ID: " + nodeId,Toast.LENGTH_LONG).show();
                popup = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("FALL DETECTED!")
                        .setCancelable(false)
                        .setMessage ("")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                time.cancel();
                                v1.cancel();
                                sendToast();
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                time.cancel();
                                v1.cancel();
                            }
                        }).show();

                time = new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        popup.setMessage("Would you like to contact emergency personnel?\n" +
                                (int) millisUntilFinished / 1000 + " seconds remaining");
                    }

                    @Override
                    public void onFinish() {
                        popup.cancel();
                        v1.cancel();
                        sendToast();
                    }
                }.start();
            }
        });
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.SECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendToast() {
        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE,double2ByteArray(senseArray[1]));
                    client.disconnect();
                }
            }).start();
        }
    }

    public static byte [] double2ByteArray (double value)
    {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }


  /*  public void onSensorChanged(SensorEvent event) {
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
    }*/

    /*public class SensorActivity extends Activity implements SensorEventListener {
        private final SensorManager mSensorManager;
        private final Sensor mAccelerometer;

        public SensorActivity() {
            mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        protected void onResume() {
            super.onResume();
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        protected void onPause() {
            super.onPause();
            mSensorManager.unregisterListener(this);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            //Right in here is where you put code to read the current sensor values and
            //update any views you might have that are displaying the sensor information
            //You'd get accelerometer values like this:
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;
            float mSensorX, mSensorY;
            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
            }
        }
    }*/
}