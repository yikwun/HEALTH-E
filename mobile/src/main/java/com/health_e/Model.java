package com.health_e;

import android.content.Context;

import java.util.Observable;
import java.util.Vector;

public class Model {
    private static Model singletonModel;
    private static Context mCtx;
    String userName, emerName;
    int age, emer_num;
    int hr, temp;
//    Vector<Observer> observers;

    private Model(String uname) {
        userName=uname;
    }

    public static synchronized Model getInstance(String uname ){
        if (singletonModel==null){
            singletonModel= new Model(uname);
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
