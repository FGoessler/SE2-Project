package de.sharebox.api;


import de.sharebox.file.model.Directory;
import de.sharebox.file.model.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
//import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
	private transient FileAPI fileAPI;
	private transient File tFile1;
	private transient File tFile2;
	private transient Directory tDirectory1;
	private transient Directory tDirectory2;

	@Mock
	private UserAPI mockedUserAPI;

	@Before
	public void setUp() {
		fileAPI = FileAPI.getUniqueInstance();

		tFile1 = new File(mockedUserAPI);
		tFile1.setIdentifier(42);
		tFile1.setName("The answer to the Ultimate Question of Life, the Universe, and Everything.");

		tFile2 = new File(mockedUserAPI);
		tFile2.setIdentifier(43);
		tFile2.setName("Not quite.");

		tDirectory1 = new Directory(mockedUserAPI);
		tDirectory1.setIdentifier(44);
		tDirectory1.setName("Tempus Fugit");

		tDirectory2 = new Directory(mockedUserAPI);
		tDirectory2.setIdentifier(45);
		tDirectory2.setName("Heart of the Sunrise");
	}

	@After
	public void tearDown() {
		fileAPI.deleteFile(tFile1);
		fileAPI.deleteFile(tFile2);
		fileAPI.deleteDirectory(tDirectory1);
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
		fileAPI.createNewFile(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(42)).isInstanceOf(File.class);
		assertThat(fileAPI.getFEntryWithId(42).getName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");
	}

	@Test
	public void creatingDuplicatesCannotBePerformed() {
		fileAPI.createNewFile(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);

		assertThat(fileAPI.createNewFile(tFile1)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
	public void testUpdatingFile() {
		fileAPI.createNewFile(tFile1);
		fileAPI.createNewFile(tFile2);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(42).getName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");

		tFile1.setName("some name");
		fileAPI.updateFile(tFile1);

		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(3);
		assertThat(fileAPI.getFEntryWithId(42).getName()).isEqualTo("some name");
	}

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(fileAPI.updateFile(tFile1)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

	@Test
	public void testDeleteFile() {
		fileAPI.createNewFile(tFile1);
		fileAPI.createNewFile(tFile2);
		fileAPI.updateFile(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(3);

		fileAPI.deleteFile(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getVersionCount()).isEqualTo(1);        //deleted all versions
	}

	@Test
	public void deletingNotExistingFilesCannotBePerformed() {
		fileAPI.createNewFile(tFile1);
		assertThat(fileAPI.deleteFile(tFile2)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
	public void testCreationOfNewDirectory() {
		fileAPI.createNewDirectory(tDirectory1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(44)).isInstanceOf(Directory.class);
		assertThat(fileAPI.getFEntryWithId(44).getName()).isEqualTo("Tempus Fugit");
	}

	@Test
	public void testUpdatingDirectory() {
		fileAPI.createNewDirectory(tDirectory1);
		fileAPI.createNewDirectory(tDirectory2);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(44).getName()).isEqualTo("Tempus Fugit");

		tDirectory1.setName("An other name");
		fileAPI.updateDirectory(tDirectory1);

		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(44).getName()).isEqualTo("An other name");
	}

	@Test
	public void testDeleteDirectory() {
		fileAPI.createNewDirectory(tDirectory1);
		fileAPI.createNewDirectory(tDirectory2);
		fileAPI.updateDirectory(tDirectory1);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);

		fileAPI.deleteDirectory(tDirectory1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
	public void testGetChangesSince() {
		long LastChange = System.currentTimeMillis();
		System.out.println("-- change test --");
		System.out.println(LastChange);
		fileAPI.createNewFile(tFile1);
		fileAPI.createNewDirectory(tDirectory1);
		fileAPI.createNewDirectory(tDirectory2);
		System.out.println(System.currentTimeMillis());
		assertThat(fileAPI.getChangesSince(LastChange).size()).isEqualTo(3);
		System.out.println("-- change end --");
	}
}
