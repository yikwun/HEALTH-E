package com.health_e;

import java.util.Vector;

public class Model {
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

    public void removeObserver (Observer o) {
        observers.remove (o);
    }

    public void notifyObservers() {
        for (Observer o: this.observers) {
            o.update(this);
        }
    }
}
