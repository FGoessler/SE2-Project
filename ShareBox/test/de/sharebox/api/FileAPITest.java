package de.sharebox.api;

/**
 *
 * @author Julius Mertens
 */

import de.sharebox.file.model.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

//@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
    private transient FileAPI fileAPI;
	private transient File tFile;
	private transient File tFile2;

    @Before
	public void setUp() {
		fileAPI = FileAPI.getUniqueInstance();

        tFile = new File();
        tFile.setIdentifier(42);
        tFile.setFileName("The answer to the Ultimate Question of Life, the Universe, and Everything.");

        tFile2 = new File();
        tFile2.setIdentifier(43);
        tFile2.setFileName("Not quite.");
    }

	@After
	public void tearDown() {
		fileAPI.deleteFile(tFile);
		fileAPI.deleteFile(tFile2);

		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

	@Test
    public void testCreationOfNewFile() {
        fileAPI.createNewFile(tFile);

		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(42)).isInstanceOf(File.class);
		assertThat(((File)fileAPI.getFEntryWithId(42)).getFileName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");
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
		assertThat(((File)fileAPI.getFEntryWithId(42)).getFileName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");

		tFile.setFileName("An other name");
		fileAPI.updateFile(tFile);

        assertThat(fileAPI.getFileCount()).isEqualTo(2);
        assertThat(fileAPI.getVersionCount()).isEqualTo(3);
		assertThat(((File)fileAPI.getFEntryWithId(42)).getFileName()).isEqualTo("An other name");
    }

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(fileAPI.updateFile(tFile)).isFalse();

		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

	@Test
    public void deletion() {
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

	//TODO: write test for directories!
}
