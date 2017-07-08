package com.health_e;

import java.util.Observable;
import java.util.Vector;

public class Model extends Observable{
    String name, emer;
    int age, emer_num;
    int hr, temp;
    Vector<Observer> observers;

    Model() {}

    public int getTemp() { return temp; }
    public int getHR() { return hr; }
    public void setTemp(int t) { temp = t; }
    public void setHR(int h) { hr = h; }

    public void addObserver (Observer o) {
        observers.add (o);
    }

    public void deleteObserver (Observer o) {
        observers.remove (o);
    }

    public void notifyObservers() {
        for (Observer o: this.observers) {
            o.update(this);
        }
    }
}
