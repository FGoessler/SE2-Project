package de.sharebox.file.model;

import java.util.ArrayList;
import java.util.List;

public class FEntry {
	private Integer id;
	private List<FEntryObserver> observers = new ArrayList<FEntryObserver>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void addObserver(FEntryObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(FEntryObserver observer) {
		observers.remove(observer);
	}

	protected void fireChangeNotification() {
		for(FEntryObserver observer : observers) {
			observer.fEntryChangedNotification(this);
		}
	}

	protected void fireDeleteNotification() {
		for(FEntryObserver observer : observers) {
			observer.fEntryDeletedNotification(this);
		}
	}
}
