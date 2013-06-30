package de.sharebox.api;

/**
 *
 * @author Julius Mertens
 */

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import de.sharebox.file.model.*;

//@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
    FileAPI fa = new FileAPI();
    File tFile;
    File tFile2;

    @Before
	public void setUp() {
        if (fa.DEBUG) {System.out.println("\n--anew.\n");}
        fa.getStats();
        tFile = new File();
        tFile.setIdentifier(42);
        tFile.setFileName("The answer to the Ultimate Question of Life, the Universe, and Everything.");
        tFile2 = new File();
        tFile2.setIdentifier(43);
        tFile2.setFileName("Not quite.");
    }

    @Test
    public void creation() {
        if (fa.DEBUG) {System.out.println("creation:");}
        int oldSize=fa.getStorageSize();
        fa.createNewFile(tFile);
        assertEquals(fa.getStorageSize(),oldSize+1);
        fa.getStats();
        fa.deleteFile(tFile);
    }

    @Test
    public void updateification() {
        if (fa.DEBUG) {System.out.println("updateification:");}
        fa.createNewFile(tFile);
        fa.createNewFile(tFile2);
        int oldSize=fa.fileCount();
        fa.updateFile(tFile);
        assertEquals(fa.fileCount(),(oldSize+1));
        fa.getStats();
        fa.deleteFile(tFile);
        fa.deleteFile(tFile2);
    }

    @Test
    public void deletion() {
        if (fa.DEBUG) {System.out.println("deletion:");}
        fa.createNewFile(tFile);
        fa.createNewFile(tFile2);
        fa.updateFile(tFile);
        int oldSize=fa.getStorageSize();
        fa.deleteFile(tFile);
        assertEquals(fa.getStorageSize(),oldSize-1);
        fa.getStats();
    }

}
