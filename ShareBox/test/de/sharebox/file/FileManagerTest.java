/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sharebox.file;

import de.sharebox.api.FileAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.File;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rainforce15
 */
public class FileManagerTest {
    FileManager fileManager = FileManager.getUniqueInstance();
    FileAPI fileAPI = FileAPI.getUniqueInstance();
    File file;
    Directory dir;
    FEntry bs;

    @Before
	public void setUp() {
        file = new File();
        file.setIdentifier(123);
        dir = new Directory();
        dir.setIdentifier(124);
        bs = new FEntry();
        bs.setIdentifier(125);
    }

	@After
	public void tearDown() {
	}

    //wip-----------------------------------------------------------------------

    @Test
    public void registerFEntryTest() {
        assertThat(fileManager.registerFEntry(file)).isTrue();
        assertThat(fileManager.registerFEntry(dir)).isTrue();
        assertThat(fileManager.registerFEntry(bs)).isFalse();
        fileAPI.deleteFile(file);
        fileAPI.deleteDirectory(dir);
    }

    @Test
    public void pollAPIForChangesTest() {
        fileAPI.createNewFile(file);
        assertThat(fileManager.getFileCount()).isEqualTo(0);
        fileManager.pollAPIForChanges();
        assertThat(fileManager.getFileCount()).isEqualTo(1);
        fileAPI.deleteFile(file);
        fileManager.deleteFEntry(file);
    }

    @Test
    public void pollFileSystemForChangesTest() {
        fileManager.updateFEntry(file);
        assertThat(fileAPI.getFileCount()).isEqualTo(0);
        fileManager.pollFileSystemForChanges();
        assertThat(fileAPI.getFileCount()).isEqualTo(1);
        fileAPI.deleteFile(file);
        fileManager.deleteFEntry(file);
    }

    @Test
    public void updateFEntryTest() {
        assertThat(fileManager.getFileCount()).isEqualTo(0);
        fileManager.updateFEntry(file);
        assertThat(fileManager.getFileCount()).isEqualTo(1);
        fileManager.updateFEntry(file);
        assertThat(fileManager.getFileCount()).isEqualTo(1);
        fileManager.deleteFEntry(file);
    }

    @Test
    public void deleteFEntryTest() {
        assertThat(fileManager.getFileCount()).isEqualTo(0);
        fileManager.updateFEntry(file);
        assertThat(fileManager.getFileCount()).isEqualTo(1);
        fileManager.deleteFEntry(file);
        assertThat(fileManager.getFileCount()).isEqualTo(0);
    }
}
