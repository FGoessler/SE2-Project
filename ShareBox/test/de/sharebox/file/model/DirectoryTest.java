package de.sharebox.file.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryTest {

	private transient Directory directory;

	@Mock
	private transient FEntryObserver observer;

	private static final String TEST_FILENAME = "TestFile";
	private static final String TEST_DIRNAME = "TestDirectory";

	@Before
	public void setUp() {
		directory = new Directory();
		directory.addObserver(observer);
	}

	@Test
	public void isFEntrySubclass() {
		assertThat(directory).isInstanceOf(FEntry.class);
	}

	@Test
	public void hasAName() {
		directory.setName(TEST_DIRNAME);
		assertThat(directory.getName()).isEqualTo(TEST_DIRNAME);

		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.NAME_CHANGED);    //assert that notification was sent
	}

	@Test
	public void canCreateNewSubFiles() {
		File createdFile = directory.createNewFile(TEST_FILENAME);

		assertThat(createdFile.getFileName()).isEqualTo(TEST_FILENAME);
		assertThat(directory.getFEntries()).contains(createdFile);

		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.ADDED_CHILDREN);    //assert that notification was sent
	}

	@Test
	public void canCreateNewSubDirectories() {
		Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);

		assertThat(createdDirectory.getName()).isEqualTo(TEST_DIRNAME);
		assertThat(directory.getFEntries()).contains(createdDirectory);

		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.ADDED_CHILDREN);    //assert that notification was sent
	}

	@Test
	public void canContainMultipleFEntries() {
		File createdFile = directory.createNewFile(TEST_FILENAME);
		Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);

		assertThat(directory.getFEntries()).hasSize(2);
		assertThat(directory.getFEntries()).contains(createdFile);
		assertThat(directory.getFEntries()).contains(createdDirectory);
	}

	@Test
	public void canRemoveFiles() {
		File createdFile = directory.createNewFile(TEST_FILENAME);
		createdFile.addObserver(observer);
		assertThat(directory.getFEntries()).contains(createdFile);

		directory.deleteFEntry(createdFile);
		assertThat(createdFile).isNotIn(directory.getFEntries());

		verify(observer, times(1)).fEntryDeletedNotification(createdFile);    	//assert that notification was sent
		//assert that notification was sent - 2 times - one for createNewFile and one for the deletion of sub objects
		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.ADDED_CHILDREN);
		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.REMOVED_CHILDREN);
	}

	@Test
	public void removesContentOfDirectoriesRecursively() {
		Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);
		createdDirectory.addObserver(observer);
		File createdFile = createdDirectory.createNewFile(TEST_FILENAME);
		createdFile.addObserver(observer);

		assertThat(directory.getFEntries()).contains(createdDirectory);
		assertThat(createdDirectory.getFEntries()).contains(createdFile);

		directory.deleteFEntry(createdDirectory);
		assertThat(createdDirectory).isNotIn(directory.getFEntries());

		verify(observer, times(1)).fEntryDeletedNotification(createdFile);         	//assert that notification was sent
		verify(observer, times(1)).fEntryDeletedNotification(createdDirectory);    	//assert that notification was sent
		//assert that notification was sent - 2 times - one for createNewFile and one for the deletion of sub objects
		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.ADDED_CHILDREN);
		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.REMOVED_CHILDREN);
	}
}
