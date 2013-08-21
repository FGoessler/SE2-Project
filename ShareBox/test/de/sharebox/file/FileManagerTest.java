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

    @Before
	public void setUp() {

    }

	@After
	public void tearDown() {
	}

    @Test
    public void registerFEntryTest() {
        FEntry file = new File();
        FileManager.registerFEntry(file);
    }
}
