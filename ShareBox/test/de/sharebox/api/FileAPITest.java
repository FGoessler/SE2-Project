package de.sharebox.api;

/**
 *
 * @author Julius Mertens
 */

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import de.sharebox.file.model.*;
import static de.sharebox.api.FileAPI.*;

//@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
    File tFile;
    File tFile2;

    @Before
	public void setUp() {
        if (DEBUG) System.out.println("\n--anew.\n");
        getStats();
        tFile = new File();
        tFile.setId(42);
        tFile.setFileName("The answer to the Ultimate Question of Life, the Universe, and Everything.");
        tFile2 = new File();
        tFile2.setId(43);
        tFile2.setFileName("Not quite.");
    }

    @Test
    public void creation() {
        if (DEBUG) System.out.println("creation:");
        int oldSize=getStorageSize();
        createNewFile(tFile);
        assertEquals(getStorageSize(),oldSize+1);
        getStats();
        deleteFile(tFile);
    }

    @Test
    public void updateification() {
        if (DEBUG) System.out.println("updateification:");
        createNewFile(tFile);
        createNewFile(tFile2);
        int oldSize=fileCount();
        updateFile(tFile);
        assertEquals(fileCount(),(oldSize+1));
        getStats();
        deleteFile(tFile);
        deleteFile(tFile2);
    }

    @Test
    public void deletion() {
        if (DEBUG) System.out.println("deletion:");
        createNewFile(tFile);
        createNewFile(tFile2);
        updateFile(tFile);
        int oldSize=getStorageSize();
        deleteFile(tFile);
        assertEquals(getStorageSize(),oldSize-1);
        getStats();
    }

}
