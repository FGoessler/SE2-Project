package de.sharebox.api;


import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileAPITest {
	public static final String NEW_NAME = "new name";

	private File tFile1;
	private File tFile2;
	private Directory tDirectory1;

	@Mock
	private User user;
	@Mock
	private UserAPI mockedUserAPI;

	@InjectMocks
	private FileAPI fileAPI;

	@Before
	public void setUp() {
		when(user.getEmail()).thenReturn("test@mail.com");
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);

		tFile1 = new File(mockedUserAPI, "Testfile", user);
		tFile2 = new File(mockedUserAPI, "Other stuff", user);
		tDirectory1 = new Directory(mockedUserAPI, "Some dir", user);
	}

	@Test
	public void testCreationAndUpdateOfNewFEntry() {
		final Long newId = fileAPI.createNewFEntry(tFile1);

		final FEntry storedFile = fileAPI.getFEntryWithId(newId);

		assertThat(storedFile).isInstanceOf(File.class)
				.isNotSameAs(tFile1);
		assertThat(storedFile.getName()).isEqualTo("Testfile");
		assertThat(storedFile.getIdentifier()).isEqualTo(newId);

		tFile1.setName("some other name");
		assertThat(fileAPI.updateFEntry(tFile1)).isTrue();

		final FEntry updatedFile = fileAPI.getFEntryWithId(newId);
		assertThat(updatedFile).isInstanceOf(File.class)
				.isNotSameAs(tFile1);
		assertThat(updatedFile.getName()).isEqualTo("some other name");
		assertThat(updatedFile.getIdentifier()).isEqualTo(newId);
	}

	@Test
	public void updatingNotExistingFileCannotBePerformed() {
		assertThat(fileAPI.updateFEntry(tFile1)).isFalse();
		assertThat(fileAPI.getFEntryWithId(0L)).isNull();
	}

	@Test
	public void testDeleteFile() {
		final Long file1Id = fileAPI.createNewFEntry(tFile1);
		final Long file2Id = fileAPI.createNewFEntry(tFile2);

		assertThat(fileAPI.deleteFEntry(tFile1)).isTrue();

		assertThat(fileAPI.getFEntryWithId(file1Id)).isNull();
		final FEntry storedFile2 = fileAPI.getFEntryWithId(file2Id);
		assertThat(storedFile2).isInstanceOf(File.class)
				.isNotSameAs(tFile1);
		assertThat(storedFile2.getName()).isEqualTo("Other stuff");
		assertThat(storedFile2.getIdentifier()).isEqualTo(file2Id);
	}

	@Test
	public void deletingNotExistingFilesCannotBePerformed() {
		assertThat(fileAPI.deleteFEntry(tFile1)).isFalse();
		assertThat(fileAPI.getFEntryWithId(0L)).isNull();
	}

	@Test
	public void testCreationAndUpdateOfNewDirectory() {
		final Long newId = fileAPI.createNewFEntry(tDirectory1);

		final FEntry storedFile = fileAPI.getFEntryWithId(newId);
		assertThat(storedFile).isInstanceOf(Directory.class)
				.isNotSameAs(tFile1);
		assertThat(storedFile.getName()).isEqualTo("Some dir");
		assertThat(storedFile.getIdentifier()).isEqualTo(newId);

		tDirectory1.setName(NEW_NAME);
		assertThat(fileAPI.updateFEntry(tDirectory1));

		final FEntry updatedFile = fileAPI.getFEntryWithId(newId);
		assertThat(updatedFile).isInstanceOf(Directory.class)
				.isNotSameAs(tFile1);
		assertThat(updatedFile.getName()).isEqualTo(NEW_NAME);
		assertThat(updatedFile.getIdentifier()).isEqualTo(newId);
	}

	@Test
	public void testGetChangesSince() {
		Long lastChange = System.currentTimeMillis();

		fileAPI.createNewFEntry(tFile1);
		fileAPI.createNewFEntry(tDirectory1);

		List<FEntry> changedFEntries = fileAPI.getChangesSince(lastChange);
		lastChange = System.currentTimeMillis();
		assertThat(changedFEntries).hasSize(2);
		assertThat(changedFEntries.get(0)).isInstanceOf(File.class)
				.isNotSameAs(tFile1);
		assertThat(changedFEntries.get(0).getName()).isEqualTo("Testfile");
		assertThat(changedFEntries.get(1)).isInstanceOf(Directory.class)
				.isNotSameAs(tDirectory1);
		assertThat(changedFEntries.get(1).getName()).isEqualTo("Some dir");

		tFile1.setName(NEW_NAME);
		fileAPI.updateFEntry(tFile1);

		changedFEntries = fileAPI.getChangesSince(lastChange);
		assertThat(changedFEntries).hasSize(1);
		assertThat(changedFEntries.get(0)).isInstanceOf(File.class)
				.isNotSameAs(tFile1);
		assertThat(changedFEntries.get(0).getName()).isEqualTo(NEW_NAME);
	}

	//TODO: test shareFEntry
}
