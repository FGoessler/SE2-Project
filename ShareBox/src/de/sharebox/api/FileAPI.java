package de.sharebox.api;

import de.sharebox.file.model.*;
import de.sharebox.user.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Julius Mertens
 */

public class FileAPI {

    //simulated storage; sublist of storage Entries is for versioning/etc. (list entry has timestamp and FEntry);
    private static List<List<storageEntry>> storage = new ArrayList<List<storageEntry>>();
    public static int getStorageSize() {return storage.size();}

    //debug flag
    public static boolean DEBUG = true;

    //various stat getters
    public static void getStats () {
        if (DEBUG) {
            System.out.println("- Storage -\n    Entries: "+storage.size()+"\n    Files: "+fileCount()+"\n");
        }
    }
    public static void getStats (int i) {
        if (DEBUG) System.out.println("- Storage: File #"+i+" -\n    entries: "+storage.get(i).size()+"\n");
    }
    public static int fileCount() {
            int storageSum = 0;
            for (int i = 0; i < storage.size(); i++) {
                storageSum+=storage.get(i).size();
            }
            return storageSum;
    }
    //debug message shortcuts
    public static void debugSuccess (String action) {
        if (DEBUG) System.out.println(action+" successful at "+System.currentTimeMillis()+".");
    }
    public static void debugFailure (String action) {
        if (DEBUG) System.out.println(action+" failed.");
    }
    public static void debugFailure (String action, String reason) {
        if (DEBUG) System.out.println(action+" failed. Reason: "+reason);
    }

    //actually relevant code-------------------------------------------------------------------------------

    public static void getUniqueInstance() {

    }

    //creates new file by looking for existing file of same ID, otherwise adds one.
    public static boolean createNewFile(File f) {

        //search through existing files to prevent duplicates, see updateFile
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==f.getId()) {
                storageEntry newEntry = new storageEntry(System.currentTimeMillis(),f);
                storage.get(i).add(newEntry);
                debugSuccess("File Creation (Update)");
                return true;
            }
        }

        //create new sublist to account for new file
        List<storageEntry> flist = new ArrayList<storageEntry>();

        //create entry with timestamp and file
        storageEntry newEntry = new storageEntry(System.currentTimeMillis(),f);
        flist.add(newEntry);

        //if this check fails you are in serious trouble
        if (storage.add(flist)) {
            debugSuccess("File Creation");
            return true;
        }
        else {
            debugFailure("File Creation","Not enough magic.");
            return false;
        }
    }

    //updates file, if found in list of existing.
    public static boolean updateFile(File f) {

        //search through existing files, see createNewFile....................
        for (int i = 0; i < storage.size(); i++) {
            //check for correct ID
            if (storage.get(i).get(0).entry.getId()==f.getId()) {
                //file found, create new version
                storageEntry newEntry = new storageEntry(System.currentTimeMillis(),f);
                storage.get(i).add(newEntry);
                debugSuccess("File Update");
                return true;
            }
        }
        debugFailure("File Update","File not found.");
        return false;
    }

    //deletes file by searching through list of existing files.
    public static boolean deleteFile(File f) {

        //search through existing files, to see if you're just confused and/or still reading this
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==f.getId()) {
                storage.remove(i);
                debugSuccess("File Deletion");
                return true;
            }
        }
        debugFailure("File Deletion","File not found.");
        return false;
    }

    public static boolean createNewDirectory(Directory d) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==d.getId()) {
                storage.get(i).get(0).entry=d;
                storage.get(i).get(0).timestamp=System.currentTimeMillis();
                debugSuccess("Directory Creation (Update)");
                return true;
            }
        }
        List<storageEntry> flist = new ArrayList<storageEntry>();
        storageEntry newEntry = new storageEntry(System.currentTimeMillis(),d);
        flist.add(newEntry);
        if (storage.add(flist)) {
            debugSuccess("Directory Creation");
            return true;
        }
        else {
            debugFailure("Directory Creation","Not enough magic.");
            return false;
        }
    }

    public static boolean updateDirectory(Directory d) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==d.getId()) {
                storage.get(i).get(0).entry=d;
                storage.get(i).get(0).timestamp=System.currentTimeMillis();
                debugSuccess("Directory Update");
                return true;
            }
        }
        debugFailure("Directory Update","File not found.");
        return false;
    }

    public static boolean deleteDiretory(Directory d) {
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==d.getId()) {
                storage.remove(i);
                debugSuccess("Directory Deletion");
                return true;
            }
        }
        debugFailure("Directory Deletion","File not found.");
        return false;
    }

    public static boolean addPermission(FEntry f, User u,FEntryPermission fp) {

        return true;
    }

    public static boolean changePermission(FEntryPermission fp) {

        return true;
    }

    public static boolean deletePermission(FEntryPermission fp) {

        return true;
    }

    //searches through entries and picks out those with a timestamp later than the given one.
    public static List getChangesSince(long x) {
        List<FEntry> changedFiles = new ArrayList<FEntry>();
        for (int i = 0; i < storage.size(); i++) {
            for (int j = 0; j < storage.get(i).size(); j++) {
                if (storage.get(i).get(j).timestamp > x) {
                    changedFiles.add(storage.get(i).get(j).entry);
                }
            }
        }
        return changedFiles;
    }
}
