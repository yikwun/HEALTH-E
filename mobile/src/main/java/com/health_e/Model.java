package com.health_e;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Model implements Serializable {
    private static final String mfileName = "appData";
    private static final String mHistoryName = "history";
    private static Model singletonModel;
    private static Context mCtx;
    private static File mfile;
    private static File mHistory;
    private String name = "def_name", emer_name = "def_contact", age = "def_age", emer_num = "def_contact_num";
    private int hr, temp, bp;
    private double lat, lon;

    private Model(Context context) {

        readfromFile(context);
    }

    public static synchronized Model getInstance(Context context) {
        if (singletonModel == null) {
            singletonModel = new Model(context);
        }
        return singletonModel;
    }

    public void savetoFile(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(mfileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream((fileOutputStream));
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("Model", "FileNotFound");

            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    public void readfromFile(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(mfileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            singletonModel = (Model) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            this.name = singletonModel.name;
            this.age = singletonModel.age;
            this.emer_name = singletonModel.emer_name;
            this.emer_num = singletonModel.emer_num;
        } catch (FileNotFoundException e) {
            Log.e("Model", "FileNotFound");
            mfile = new File(context.getFilesDir(), mfileName);
            e.printStackTrace();
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getBP() { return bp; }

    public int getTemp() {
        return temp;
    }

    public int getHR() {
        return hr;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getEmerName() {
        return emer_name;
    }

    public String getEmerNum() {
        return emer_num;
    }

    public String getLocation(Context context) {
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String prov = addresses.get(0).getAdminArea();
            String loc = address.concat(", " + city + ", " + prov);
            return loc;
        } catch (Exception e) {
        }
        return "location unavailable";
    }

    public void setBP (int b) {
        bp = b;
    }

    public void setTemp(int t) {
        temp = t;
    }

    public void setHR(int h) {
        hr = h;
    }

    public void setName(String s) {
        name = s;
    }

    public void setAge(String s) {
        age = s;
    }

    public void setEmerName(String s) {
        emer_name = s;
    }

    public void setEmerNum(String s) {
        emer_num = s;
    }

    public void setLocation(double la, double lo) {
        lat = la;
        lon = lo;
    }
}

