package com.health_e;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Vector;

public class Model {
    private static final String mfileName="appData";
    private static final String mHistoryName="history";
    private static Model singletonModel;
    private static Context mCtx;
    private static File mfile;
    private static File mHistory;
    String userName, emerName;
    int age, emer_num;
    int hr, temp;
//    Vector<Observer> observers;

    private Model(Context context, String username) {
        userName=username;
        //
        FileOutputStream outputStream;
        try{
            outputStream=context.openFileOutput(mfileName,Context.MODE_PRIVATE);

            outputStream.write(userName.getBytes());

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



    public static synchronized Model getInstance(Context context,String uname ){
        if (singletonModel==null){
            singletonModel= new Model(context,uname);
        }
        return singletonModel;
    }


    public int getTemp() { return temp; }
    public int getHR() { return hr; }
    public void setTemp(int t) { temp = t; }
    public void setHR(int h) { hr = h; }
    public String getUserName(){
        return userName;
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
