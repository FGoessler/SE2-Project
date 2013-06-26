package de.sharebox.file.model;

import java.util.ArrayList;
import java.util.List;

public class Directory extends FEntry {
    private List<FEntry> fEntries = new ArrayList<FEntry>();
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public File createNewFile(String filename) {
        File newFile = new File();
        newFile.setFileName(filename);

        fEntries.add(newFile);

        return newFile;
    }

    public Directory createNewDirectory(String dirname) {
        Directory newDir = new Directory();
        newDir.setName(dirname);

        fEntries.add(newDir);

        return newDir;
    }

    public List<FEntry> getFEntries() {
        return fEntries;
    }

    public void deleteFEntry(FEntry fEntry) {
        if(fEntry instanceof Directory) {
            Directory dir = (Directory) fEntry;
            while(dir.getFEntries().size() > 0) {
                dir.deleteFEntry(dir.getFEntries().get(0));
            }
        }

        fEntry.fireDeleteNotification();

        fEntries.remove(fEntry);
    }
}
