package com.health_e;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;

public class Model implements Parcelable {
    private static final String mfileName="appData";
    private static final String mHistoryName="history";
    private static Model singletonModel;
    private static Context mCtx;
    private static File mfile;
    private static File mHistory;
    private String name = "def_name", emer_name = "def_contact", age = "def_age", emer_num = "def_contact_num";
    private int hr, temp;
    private double lat, lon;
//    Vector<Observer> observers;

    // used for serializable or parcelable, whichever we decide to use
//    protected Object readResolve() {
//        return singletonModel;
//    }

    private Model(Context context) {
        //
        FileOutputStream outputStream;
        try{
            outputStream=context.openFileOutput(mfileName,Context.MODE_PRIVATE);

            outputStream.write(name.getBytes());

        }catch (FileNotFoundException e){
            mfile= new File(context.getFilesDir(),mfileName);
            try {
                mfile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


        //
    }



    public static synchronized Model getInstance(Context context){
        if (singletonModel==null){
            singletonModel= new Model(context);
        }
        return singletonModel;
    }


    public int getTemp() { return temp; }
    public int getHR() { return hr; }
    public String getName(){
        return name;
    }
    public String getAge() { return age; }
    public String getEmerName() { return emer_name; }
    public String getEmerNum () { return emer_num; }
    public String getLocation() {
        // need to determine location and put into string rather than keep lat and lon
        return "";
    }

    public void setTemp(int t) { temp = t; }
    public void setHR(int h) { hr = h; }
    public void setName (String s) { name = s; }
    public void setAge (String s) { age = s; }
    public void setEmerName (String s) { emer_name = s; }
    public void setEmerNum (String s) { emer_num = s; }
    public void setLocation (double la, double lo) {
        lat = la;
        lon = lo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(emer_name);
        dest.writeString(emer_num);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RegistrationPogo createFromParcel(Parcel in) {
            return new RegistrationPogo(in);
        }
        public RegistrationPogo[] newArray(int size) {
            return new RegistrationPogo[size];
        }
    };

    private static class RegistrationPogo {
        RegistrationPogo(Parcel in) {

        }
    }
//    public void addObserver (Observer o) {
//        observers.add (o);
//    }
//
//    public void deleteObserver (Observer o) {
//        observers.remove (o);
//    }
//
//    public void notifyObservers() {
//        for (Observer o: this.observers) {
//            o.update(this);
//        }
    }
