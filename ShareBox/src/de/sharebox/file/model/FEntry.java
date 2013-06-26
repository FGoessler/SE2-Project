package de.sharebox.file.model;

import java.util.ArrayList;
import java.util.List;

public class FEntry {
    private Integer id;
    private List<FEntryObserver> observers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void addObserver(FEntryObserver observer) {
        if(observers == null) {
            observers = new ArrayList<FEntryObserver>();
        }
        observers.add(observer);
    }

    public void removeObserver(FEntryObserver observer) {
        if(observers != null){
            observers.remove(observer);
        }
    }

    public void fireChangeNotification() {
        for(FEntryObserver observer : observers) {
            observer.fEntryChangedNotification(this);
        }
    }

    public void fireDeleteNotification() {
        for(FEntryObserver observer : observers) {
            observer.fEntryDeletedNotification(this);
        }
    }
}
