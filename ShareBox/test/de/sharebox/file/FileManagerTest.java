package de.sharebox.file;

import de.sharebox.api.FileAPI;
import de.sharebox.api.UserAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FileManagerTest {

	private File file;
	private Directory dir;
	private FEntry nothing;
	private FileManager fileManager;

	@InjectMocks
	private UserAPI mockedUserAPI;
	@InjectMocks
	private FileAPI fileAPI;

	@Before
	public void setUp() {
		fileManager = new FileManager(fileAPI);
		file = new File(mockedUserAPI);
		file.setIdentifier(123L);
		dir = new Directory(mockedUserAPI);
		dir.setIdentifier(124L);
		nothing = new FEntry(mockedUserAPI);
		nothing.setIdentifier(125L);
	}

	//TODO: test FileManager

	@Test
	public void registerFEntryTest() {
		assertThat(fileManager.registerFEntry(file)).isTrue();
		assertThat(fileManager.registerFEntry(dir)).isTrue();
		assertThat(fileManager.registerFEntry(nothing)).isFalse();
		fileAPI.deleteFEntry(file);
		fileAPI.deleteFEntry(dir);
	}

	@Test
	public void pollAPIForChangesTest() {

	}

	@Test
	public void pollFileSystemForChangesTest() {

	}

	@Test
	public void updateFEntryTest() {

	}

	@Test
	public void deleteFEntryTest() {

	}
}
