package de.sharebox.api;


import de.sharebox.file.model.Directory;
import de.sharebox.file.model.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
	private File tFile1;
	private File tFile2;
	private Directory tDirectory1;
	private Directory tDirectory2;
	private FileAPI fileAPI;

	@InjectMocks
	private UserAPI mockedUserAPI;


	@Before
	public void setUp() {
		fileAPI = new FileAPI();
		tFile1 = new File(mockedUserAPI);
		tFile1.setIdentifier(42L);
		tFile1.setName("The answer to the Ultimate Question of Life, the Universe, and Everything.");


		tFile2 = new File(mockedUserAPI);
		tFile2.setIdentifier(43L);
		tFile2.setName("Not quite.");

		tDirectory1 = new Directory(mockedUserAPI);
		tDirectory1.setIdentifier(44L);
		tDirectory1.setName("Tempus Fugit");

		tDirectory2 = new Directory(mockedUserAPI);
		tDirectory2.setIdentifier(45L);
		tDirectory2.setName("Heart of the Sunrise");
		System.out.println("asd" + tFile1.getName());
	}

	@After
	public void tearDown() {
		fileAPI.deleteFEntry(tFile1);
		fileAPI.deleteFEntry(tFile2);
		fileAPI.deleteFEntry(tDirectory1);
		fileAPI.deleteFEntry(tDirectory2);

		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

	//TODO: test FileAPI

	@Test
	public void testCreationOfNewFile() {
		fileAPI.createNewFEntry(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(42L)).isInstanceOf(File.class);
		assertThat(fileAPI.getFEntryWithId(42L).getName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");
	}

	@Test
	public void creatingDuplicatesCannotBePerformed() {
		fileAPI.createNewFEntry(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);

		assertThat(fileAPI.createNewFEntry(tFile1)).isGreaterThan(0);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
	public void testUpdatingFile() {
		fileAPI.createNewFEntry(tFile1);
		fileAPI.createNewFEntry(tFile2);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(42L).getName()).isEqualTo("The answer to the Ultimate Question of Life, the Universe, and Everything.");

		tFile1.setName("some name");
		fileAPI.updateFEntry(tFile1);

		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(3);
		assertThat(fileAPI.getFEntryWithId(42L).getName()).isEqualTo("some name");
	}

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(fileAPI.updateFEntry(tFile1)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(0);
	}

	@Test
	public void testDeleteFile() {
		fileAPI.createNewFEntry(tFile1);
		fileAPI.createNewFEntry(tFile2);
		fileAPI.updateFEntry(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getVersionCount()).isEqualTo(3);

		fileAPI.deleteFEntry(tFile1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getVersionCount()).isEqualTo(1);        //deleted all versions
	}

	@Test
	public void deletingNotExistingFilesCannotBePerformed() {
		fileAPI.createNewFEntry(tFile1);
		assertThat(fileAPI.deleteFEntry(tFile2)).isFalse();
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
	public void testCreationOfNewDirectory() {
		fileAPI.createNewFEntry(tDirectory1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
		assertThat(fileAPI.getFEntryWithId(44L)).isInstanceOf(Directory.class);
		assertThat(fileAPI.getFEntryWithId(44L).getName()).isEqualTo("Tempus Fugit");
	}

	@Test
	public void testUpdatingDirectory() {
		fileAPI.createNewFEntry(tDirectory1);
		fileAPI.createNewFEntry(tDirectory2);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(44L).getName()).isEqualTo("Tempus Fugit");

		tDirectory1.setName("An other name");
		fileAPI.updateFEntry(tDirectory1);

		assertThat(fileAPI.getFileCount()).isEqualTo(2);
		assertThat(fileAPI.getFEntryWithId(44L).getName()).isEqualTo("An other name");
	}

	@Test
	public void testDeleteDirectory() {
		fileAPI.createNewFEntry(tDirectory1);
		fileAPI.createNewFEntry(tDirectory2);
		fileAPI.updateFEntry(tDirectory1);
		assertThat(fileAPI.getFileCount()).isEqualTo(2);

		fileAPI.deleteFEntry(tDirectory1);
		assertThat(fileAPI.getFileCount()).isEqualTo(1);
	}

	@Test
	public void testGetChangesSince() {
		final long lastChange = System.currentTimeMillis();
		System.out.println("-- change test --");
		System.out.println(lastChange);
		fileAPI.createNewFEntry(tFile1);
		fileAPI.createNewFEntry(tDirectory1);
		fileAPI.createNewFEntry(tDirectory2);
		System.out.println(System.currentTimeMillis());
		assertThat(fileAPI.getChangesSince(lastChange).size()).isEqualTo(3);
		System.out.println("-- change end --");
	}
}
