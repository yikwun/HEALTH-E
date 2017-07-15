package com.health_e;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Detection extends Service {
    public Detection() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
