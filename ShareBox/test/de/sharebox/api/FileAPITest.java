package de.sharebox.api;

/**
 *
 * @author Julius Mertens
 */

import de.sharebox.file.model.File;
import de.sharebox.file.model.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
//import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
    private transient FileAPI fileAPI;
	private transient File tFile;
	private transient File tFile2;
	private transient Directory tDirectory;
	private transient Directory tDirectory2;

    @Before
	public void setUp() {
		fileAPI = FileAPI.getUniqueInstance();

        tFile = new File();
        tFile.setIdentifier(42);
        tFile.setName("The answer to the Ultimate Question of Life, the Universe, and Everything.");

        tFile2 = new File();
        tFile2.setIdentifier(43);
        tFile2.setName("Not quite.");

        tDirectory = new Directory();
        tDirectory.setIdentifier(44);
        tDirectory.setName("Tempus Fugit");

        tDirectory2 = new Directory();
        tDirectory2.setIdentifier(45);
        tDirectory2.setName("Heart of the Sunrise");
    }

	@After
	public void tearDown() {
		fileAPI.deleteFile(tFile);
		fileAPI.deleteFile(tFile2);
		fileAPI.deleteDirectory(tDirectory);
		fileAPI.deleteDirectory(tDirectory2);

		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

/*	@Test
	public void isASingletonButCanInjectMocks() {
		assertThat(FileAPI.getUniqueInstance()).isNotNull();

		FileAPI mockedFileAPI = mock(FileAPI.class);
		FileAPI.injectSingletonInstance(mockedFileAPI);

		assertThat(FileAPI.getUniqueInstance()).isSameAs(mockedFileAPI);
	}*/

	@Test
    public void testCreationOfNewFile() {
        fileAPI.createNewFile(tFile);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(42)).isInstanceOf(File.class);
		assertThat(fileAPI.getFEntryWithId(42).getName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");
    }

	@Test
	public void creatingDuplicatesCannotBePerformed() {
		fileAPI.createNewFile(tFile);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);

		assertThat(fileAPI.createNewFile(tFile)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
    public void testUpdatingFile() {
        fileAPI.createNewFile(tFile);
        fileAPI.createNewFile(tFile2);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(42).getName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");

		tFile.setName("some name");
		fileAPI.updateFile(tFile);

        assertThat(fileAPI.getFileCount()).isEqualTo(2);
        assertThat(fileAPI.getVersionCount()).isEqualTo(3);
		assertThat(fileAPI.getFEntryWithId(42).getName()).isEqualTo("some name");
    }

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(fileAPI.updateFile(tFile)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

	@Test
    public void testDeleteFile() {
        fileAPI.createNewFile(tFile);
        fileAPI.createNewFile(tFile2);
        fileAPI.updateFile(tFile);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(3);

        fileAPI.deleteFile(tFile);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getVersionCount()).isEqualTo(1);		//deleted all versions
    }

	@Test
	public void deletingNotExistingFilesCannotBePerformed() {
		fileAPI.createNewFile(tFile);
		assertThat(fileAPI.deleteFile(tFile2)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

    @Test
    public void testCreationOfNewDirectory() {
        fileAPI.createNewDirectory(tDirectory);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(44)).isInstanceOf(Directory.class);
		assertThat(fileAPI.getFEntryWithId(44).getName()).isEqualTo("Tempus Fugit");
    }

    @Test
    public void testUpdatingDirectory() {
        fileAPI.createNewDirectory(tDirectory);
        fileAPI.createNewDirectory(tDirectory2);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(44).getName()).isEqualTo("Tempus Fugit");

		tDirectory.setName("An other name");
		fileAPI.updateDirectory(tDirectory);

        assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(44).getName()).isEqualTo("An other name");
    }

    @Test
    public void testDeleteDirectory() {
        fileAPI.createNewDirectory(tDirectory);
        fileAPI.createNewDirectory(tDirectory2);
        fileAPI.updateDirectory(tDirectory);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);

        fileAPI.deleteDirectory(tDirectory);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
    }

    @Test
    public void testGetChangesSince() {
        int oldSize = fileAPI.getChangesSince(System.currentTimeMillis()).size();
        fileAPI.createNewFile(tFile);
        fileAPI.createNewDirectory(tDirectory);
        fileAPI.createNewDirectory(tDirectory2);
        assertThat(fileAPI.getChangesSince(oldSize).size()).isEqualTo(3);
    }
}
