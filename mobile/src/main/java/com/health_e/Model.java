package com.health_e;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;

public class Model implements Serializable {
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
    public double getLat() { return lat; }
    public double getLon() { return lon; }

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
