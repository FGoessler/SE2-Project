package de.sharebox.file.model;

public interface FEntryObserver {
    void fEntryChangedNotification(FEntry fEntry);

    void fEntryDeletedNotification(FEntry fEntry);
}
