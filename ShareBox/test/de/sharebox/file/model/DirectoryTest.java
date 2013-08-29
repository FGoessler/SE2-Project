package de.sharebox.file.model;

import com.google.common.collect.ImmutableList;
import de.sharebox.api.UserAPI;
import de.sharebox.file.FileManager;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.DirectoryObserver;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryTest {

	private Directory directory;

	@Mock
	private DirectoryObserver observer;
	@Mock
	private User mockedUser;
	@Mock
	private UserAPI mockedUserAPI;

	private static final String TEST_FILENAME = "TestFile";
	private static final String TEST_DIRNAME = "TestDirectory";

	@Before
	public void setUp() {
		directory = new Directory(mockedUserAPI, "ParentDir", mockedUser);
		directory.addObserver(observer);

		when(mockedUserAPI.getCurrentUser()).thenReturn(mockedUser);
	}

	@Test
	public void isFEntrySubclass() {
		assertThat(directory).isInstanceOf(FEntry.class);
	}

	@Test
	public void hasACopyConstructor() {
		directory.setName(TEST_DIRNAME);
		directory.setIdentifier(1234L);
		directory.createNewFile(TEST_FILENAME);
		directory.createNewDirectory(TEST_DIRNAME + "2");

		final Directory copy = new Directory(directory);

		assertThat(copy).isNotSameAs(directory);
		assertThat(copy.getFEntries()).isNotSameAs(directory.getFEntries());
		assertThat(copy.getName()).isEqualTo(directory.getName());
		assertThat(copy.getIdentifier()).isEqualTo(directory.getIdentifier());

		assertThat(copy.getFEntries().size()).isEqualTo(directory.getFEntries().size());
		assertThat(copy.getFEntries().get(0).getName()).isEqualTo(directory.getFEntries().get(0).getName());
		assertThat(copy.getFEntries().get(1).getName()).isEqualTo(directory.getFEntries().get(1).getName());
	}

	@Test
	public void canCreateNewSubFiles() {
		final File createdFile = directory.createNewFile(TEST_FILENAME).get();

		assertThat(createdFile.getName()).isEqualTo(TEST_FILENAME);
		assertThat(directory.getFEntries()).contains(createdFile);

		//check that initial permission was set
		assertThat(createdFile.getPermissions()).hasSize(1);
		final Permission permission = createdFile.getPermissions().get(0);
		assertThat(permission.getUser()).isSameAs(mockedUser);
		assertThat(permission.getFEntry()).isSameAs(createdFile);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

		assertThat(createdFile.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CREATED);
		assertThat(directory.getLogEntries().get(1).getMessage()).isEqualTo(LogEntry.LogMessage.ADDED_FILE);

		final DirectoryNotification expectedNotification = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, directory, ImmutableList.<FEntry>of(createdFile));
		verify(observer, times(1)).directoryNotification(expectedNotification);    //assert that notification was sent
	}

	@Test
	public void canCreateNewSubDirectories() {
		final Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME).get();

		assertThat(createdDirectory.getName()).isEqualTo(TEST_DIRNAME);
		assertThat(directory.getFEntries()).contains(createdDirectory);

		//check that initial permission was se
		assertThat(createdDirectory.getPermissions()).hasSize(1);
		final Permission permission = createdDirectory.getPermissions().get(0);
		assertThat(permission.getUser()).isSameAs(mockedUser);
		assertThat(permission.getFEntry()).isSameAs(createdDirectory);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

		assertThat(createdDirectory.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CREATED);
		assertThat(directory.getLogEntries().get(1).getMessage()).isEqualTo(LogEntry.LogMessage.ADDED_DIRECTORY);

		final DirectoryNotification expectedNotification = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, directory, ImmutableList.<FEntry>of(createdDirectory));
		verify(observer, times(1)).directoryNotification(expectedNotification);    //assert that notification was sent
	}

	@Test
	public void cannotCreateFEntriesWithAlreadyUsedNames() {
		assertThat(directory.createNewDirectory(TEST_DIRNAME).isPresent()).isTrue();

		assertThat(directory.createNewDirectory(TEST_DIRNAME).isPresent()).isFalse();
		assertThat(directory.createNewFile(TEST_DIRNAME).isPresent()).isFalse();
	}

	@Test
	public void canContainMultipleFEntries() {
		final File createdFile = directory.createNewFile(TEST_FILENAME).get();
		final Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME).get();

		assertThat(directory.getFEntries()).hasSize(2);
		assertThat(directory.getFEntries()).contains(createdFile);
		assertThat(directory.getFEntries()).contains(createdDirectory);
	}

	@Test
	public void canAddExistingFEntries() {
		final File newFile = new File(mockedUserAPI, TEST_FILENAME, mockedUser);

		directory.addFEntry(newFile);

		assertThat(directory.getFEntries()).contains(newFile);
		assertThat(directory.getLogEntries().get(1).getMessage()).isEqualTo(LogEntry.LogMessage.ADDED_FILE);
		final DirectoryNotification expectedNotification1 = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, directory, ImmutableList.<FEntry>of(newFile));
		verify(observer, times(1)).directoryNotification(expectedNotification1);    //assert that notification was sent

		final Directory newDirectory = new Directory(mockedUserAPI, TEST_DIRNAME, mockedUser);

		directory.addFEntry(newDirectory);

		assertThat(directory.getFEntries()).contains(newDirectory);
		assertThat(directory.getLogEntries().get(2).getMessage()).isEqualTo(LogEntry.LogMessage.ADDED_DIRECTORY);
		final DirectoryNotification expectedNotification2 = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, directory, ImmutableList.<FEntry>of(newDirectory));
		verify(observer, times(1)).directoryNotification(expectedNotification2);    //assert that notification was sent
	}

	@Test
	public void canRemoveFiles() {
		final File createdFile = directory.createNewFile(TEST_FILENAME).get();
		createdFile.addObserver(observer);
		assertThat(directory.getFEntries()).contains(createdFile);

		directory.deleteFEntry(createdFile);
		assertThat(createdFile).isNotIn(directory.getFEntries());

		final FEntryNotification expectedNotification1 = new FEntryNotification(createdFile, FEntryNotification.ChangeType.DELETED, directory);
		verify(observer, times(1)).fEntryNotification(expectedNotification1);        //assert that notification was sent
		//assert that notification was sent - 2 times - one for createNewFEntry and one for the deletion of sub objects
		final DirectoryNotification expectedNotification2 = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, directory, ImmutableList.<FEntry>of(createdFile));
		verify(observer, times(1)).directoryNotification(expectedNotification2);
		final DirectoryNotification expectedNotification3 = new DirectoryNotification(directory, FEntryNotification.ChangeType.REMOVE_CHILDREN, directory, ImmutableList.<FEntry>of(createdFile));
		verify(observer, times(1)).directoryNotification(expectedNotification3);

		assertThat(directory.getLogEntries().get(2).getMessage()).isEqualTo(LogEntry.LogMessage.REMOVED_FILE);
	}

	@Test
	public void removesContentOfDirectoriesRecursively() {
		final Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME).get();
		createdDirectory.addObserver(observer);
		final File createdFile = createdDirectory.createNewFile(TEST_FILENAME).get();
		createdFile.addObserver(observer);

		assertThat(directory.getFEntries()).contains(createdDirectory);
		assertThat(createdDirectory.getFEntries()).contains(createdFile);

		directory.deleteFEntry(createdDirectory);
		assertThat(createdDirectory).isNotIn(directory.getFEntries());

		final FEntryNotification expectedNotification1 = new FEntryNotification(createdFile, FEntryNotification.ChangeType.DELETED, createdDirectory);
		verify(observer, times(1)).fEntryNotification(expectedNotification1);            //assert that notification was sent
		final FEntryNotification expectedNotification2 = new FEntryNotification(createdDirectory, FEntryNotification.ChangeType.DELETED, directory);
		verify(observer, times(1)).fEntryNotification(expectedNotification2);        //assert that notification was sent
		//assert that notification was sent - 2 times - one for createNewFEntry and one for the deletion of sub objects
		final DirectoryNotification expectedNotification3 = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, directory, ImmutableList.<FEntry>of(createdDirectory));
		verify(observer, times(1)).directoryNotification(expectedNotification3);
		final DirectoryNotification expectedNotification4 = new DirectoryNotification(directory, FEntryNotification.ChangeType.REMOVE_CHILDREN, directory, ImmutableList.<FEntry>of(createdDirectory));
		verify(observer, times(1)).directoryNotification(expectedNotification4);

		assertThat(directory.getLogEntries().get(2).getMessage()).isEqualTo(LogEntry.LogMessage.REMOVED_DIRECTORY);
	}

	@Test
	public void testApplyChangesFormAPI() {
		directory.setIdentifier(1234L);
		File removedFile = directory.createNewFile("removed file").get();
		removedFile.setIdentifier(1L);
		File keptFile = directory.createNewFile("kept file").get();
		keptFile.setIdentifier(2L);
		final Directory updatedDirectory = new Directory(mockedUserAPI, "newDirName", mockedUser);
		updatedDirectory.addFEntry(keptFile);
		Directory newDir = updatedDirectory.createNewDirectory("added dir").get();
		newDir.setIdentifier(3L);

		directory.applyChanges(updatedDirectory, mock(FileManager.class));

		assertThat(directory).isNotSameAs(updatedDirectory);
		assertThat(directory.getName()).isEqualTo("newDirName");
		assertThat(directory.getIdentifier()).isEqualTo(1234L);
		assertThat(directory.getFEntries()).hasSize(2);
		assertThat(directory.getFEntries().get(0).getIdentifier()).isEqualTo(2L);
		assertThat(directory.getFEntries().get(0).getName()).isEqualTo("kept file");
		assertThat(directory.getFEntries().get(1).getIdentifier()).isEqualTo(3L);
		assertThat(directory.getFEntries().get(1).getName()).isEqualTo("added dir");
	}
}
