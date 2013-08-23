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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FileManagerTest {

	private File file;
	private Directory dir;
	private FEntry bs;

	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private FileAPI fileAPI;

	@InjectMocks
	private FileManager fileManager;

	@Before
	public void setUp() {
		file = new File(mockedUserAPI);
		file.setIdentifier(123);
		dir = new Directory(mockedUserAPI);
		dir.setIdentifier(124);
		bs = new FEntry(mockedUserAPI);
		bs.setIdentifier(125);
	}

	//wip-----------------------------------------------------------------------

	@Test
	public void registerFEntryTest() {
		//TODO: schlägt fehl aber man weiß auch nicht so recht was hier getestet wird?
		// Eigentlich sollte ja geprüft werden, das der FileManager ab jetzt auf Notifications reagiert und die API triggert
		assertThat(fileManager.registerFEntry(file)).isTrue();
		assertThat(fileManager.registerFEntry(dir)).isTrue();
		assertThat(fileManager.registerFEntry(bs)).isFalse();
		fileAPI.deleteFile(file);
		fileAPI.deleteDirectory(dir);
	}

	@Test
	public void pollAPIForChangesTest() {
		//TODO: schlägt fehl aber man weiß auch nicht so recht was hier getestet wird?
		fileAPI.createNewFile(file);
		assertThat(fileManager.getFileCount()).isEqualTo(0);
		fileManager.pollAPIForChanges();
		assertThat(fileManager.getFileCount()).isEqualTo(1);
		fileAPI.deleteFile(file);
		fileManager.deleteFEntry(file);
	}

	@Test
	public void pollFileSystemForChangesTest() {
		//TODO: schlägt fehl aber man weiß auch nicht so recht was hier getestet wird?
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
