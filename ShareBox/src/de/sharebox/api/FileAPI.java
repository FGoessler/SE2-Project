package de.sharebox.api;

import de.sharebox.file.model.*;
import de.sharebox.user.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Julius Mertens
 */

public class FileAPI {

    public class storageEntry {
        public long timestamp;
        public FEntry entry;
        public storageEntry(long time, FEntry add) {
            timestamp = time;
            entry = add;
        }
    }

    //simulated storage; sublist of storage Entries is for versioning/etc. (list entry has timestamp and FEntry);
    private List<List<storageEntry>> storage = new ArrayList<List<storageEntry>>();
    public int getStorageSize() {return storage.size();}
    private String fnf = "File not found.";

    //debug flag
    public boolean DEBUG = true;

    //various stat getters
    public void getStats () {
        if (DEBUG) {
            System.out.println("- Storage -\n    Entries: "+storage.size()+"\n    Files: "+fileCount()+"\n");
        }
    }
    public void getStats (int i) {
        if (DEBUG) {System.out.println("- Storage: File #"+i+" -\n    entries: "+storage.get(i).size()+"\n");}
    }
    public int fileCount() {
            int storageSum = 0;
            for (int i = 0; i < storage.size(); i++) {
                storageSum+=storage.get(i).size();
            }
            return storageSum;
    }
    //debug message shortcuts
    public void debugSuccess (String action) {
        if (DEBUG) {System.out.println(action+" successful at "+System.currentTimeMillis()+".");}
    }
    public void debugFailure (String action) {
        if (DEBUG) {System.out.println(action+" failed.");}
    }
    public void debugFailure (String action, String reason) {
        if (DEBUG) {System.out.println(action+" failed. Reason: "+reason);}
    }

    //actually relevant code-------------------------------------------------------------------------------

    private static FileAPI instance = new FileAPI();

    public static FileAPI getUniqueInstance() {
        return instance;
    }

    /**creates new file by looking for existing file of same ID, otherwise adds one.
     * @param f zu erzeugendes File
     * @return ob erfolgreich**/
    public boolean createNewFile(File f) {
        int pmdFriendyReturn = -1;

        //search through existing files to prevent duplicates, see updateFile
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==f.getId()) {
                storageEntry newEntry = new storageEntry(System.currentTimeMillis(),f);
                storage.get(i).add(newEntry);
                debugSuccess("File Creation (Update)");
                if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
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
            if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
        }
        else {
            debugFailure("File Creation","Not enough magic.");
            if(pmdFriendyReturn==-1) {pmdFriendyReturn = 0;}
        }
        return pmdFriendyReturn==1;
    }


    /**updates file, if found in list of existing.
     * @param f zu bearbeitendes File
     * @return ob erfolgreich**/
    public boolean updateFile(File f) {
        int pmdFriendyReturn = -1;

        //search through existing files, see createNewFile....................
        for (int i = 0; i < storage.size(); i++) {
            //check for correct ID
            if (storage.get(i).get(0).entry.getId()==f.getId()) {
                //file found, create new version
                storageEntry newEntry = new storageEntry(System.currentTimeMillis(),f);
                storage.get(i).add(newEntry);
                debugSuccess("File Update");
                if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
            }
        }
        debugFailure("File Update",fnf);
        return pmdFriendyReturn==1;
    }

    /**deletes file by searching through list of existing files.
     * @param f zu löschendes File
     * @return ob erfolgreich**/
    public boolean deleteFile(File f) {
        int pmdFriendyReturn = -1;

        //search through existing files, to see if you're just confused and/or still reading this
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==f.getId()) {
                storage.remove(i);
                debugSuccess("File Deletion");
                if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
            }
        }
        debugFailure("File Deletion",fnf);
        if(pmdFriendyReturn==-1) {pmdFriendyReturn = 0;}
        return pmdFriendyReturn==1;
    }

    /**@param d zu erzeugendes Directory
     * @return ob erfolgreich**/
    public boolean createNewDirectory(Directory d) {
        int pmdFriendyReturn = -1;
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==d.getId()) {
                storage.get(i).get(0).entry=d;
                storage.get(i).get(0).timestamp=System.currentTimeMillis();
                debugSuccess("Directory Creation (Update)");
                if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
            }
        }
        List<storageEntry> flist = new ArrayList<storageEntry>();
        storageEntry newEntry = new storageEntry(System.currentTimeMillis(),d);
        flist.add(newEntry);
        if (storage.add(flist)) {
            debugSuccess("Directory Creation");
            if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
        }
        else {
            debugFailure("Directory Creation","Not enough magic.");
            if(pmdFriendyReturn==-1) {pmdFriendyReturn = 0;}
        }
        return pmdFriendyReturn==1;
    }

    /**@param d zu bearbeitendes Directory
     * @return ob erfolgreich**/
    public boolean updateDirectory(Directory d) {
        int pmdFriendyReturn = -1;
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==d.getId()) {
                storage.get(i).get(0).entry=d;
                storage.get(i).get(0).timestamp=System.currentTimeMillis();
                debugSuccess("Directory Update");
                if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
            }
        }
        debugFailure("Directory Update","File not found.");
        if(pmdFriendyReturn==-1) {pmdFriendyReturn = 0;}
        return pmdFriendyReturn==1;
    }

    /**@param d zu löschendes Directory
     * @return ob erfolgreich**/
    public boolean deleteDiretory(Directory d) {
        int pmdFriendyReturn = -1;
        for (int i = 0; i < storage.size(); i++) {
            if (storage.get(i).get(0).entry.getId()==d.getId()) {
                storage.remove(i);
                debugSuccess("Directory Deletion");
                if(pmdFriendyReturn==-1) {pmdFriendyReturn = 1;}
            }
        }
        debugFailure("Directory Deletion",fnf);
        if(pmdFriendyReturn==-1) {pmdFriendyReturn = 0;}
        return pmdFriendyReturn==1;
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

    /**searches through entries and picks out those with a timestamp later than the given one.
     * @param x ist ein timestamp in ms
     * @return ob erfolgreich**/
    public List getChangesSince(long x) {
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
