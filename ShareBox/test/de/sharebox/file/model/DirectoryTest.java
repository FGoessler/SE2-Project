package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryTest {

	private transient Directory directory;

	@Mock
	private transient FEntryObserver observer;
	@Mock
	private transient User mockedUser;
	@Mock
	private transient UserAPI mockedUserAPI;

	private static final String TEST_FILENAME = "TestFile";
	private static final String TEST_DIRNAME = "TestDirectory";

	@Before
	public void setUp() {
		directory = new Directory();
		directory.addObserver(observer);

		UserAPI.injectSingletonInstance(mockedUserAPI);
		when(mockedUserAPI.getCurrentUser()).thenReturn(mockedUser);
	}

	@After
	public void tearDown() {
		UserAPI.resetSingletonInstance();
	}

	@Test
	public void isFEntrySubclass() {
		assertThat(directory).isInstanceOf(FEntry.class);
	}

	@Test
	public void hasACopyConstructor() {
		directory.setName(TEST_DIRNAME);
		directory.setIdentifier(1234);
		directory.createNewFile(TEST_FILENAME);
		directory.createNewDirectory(TEST_DIRNAME + "2");

		Directory copy = new Directory(directory);

		assertThat(copy).isNotSameAs(directory);
		assertThat(copy.getFEntries()).isNotSameAs(directory.getFEntries());
		assertThat(copy.getName()).isEqualTo(directory.getName());
		assertThat(copy.getIdentifier()).isEqualTo(directory.getIdentifier());

		assertThat(copy.getFEntries().size()).isEqualTo(directory.getFEntries().size());
		assertThat(copy.getFEntries().get(0).getName()).isEqualTo(directory.getFEntries().get(0).getName());
		assertThat(copy.getFEntries().get(1).getName()).isEqualTo(directory.getFEntries().get(1).getName());
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

		assertThat(createdFile.getName()).isEqualTo(TEST_FILENAME);
		assertThat(directory.getFEntries()).contains(createdFile);

		//check permission
		assertThat(createdFile.getPermissions()).hasSize(1);
		FEntryPermission permission = createdFile.getPermissions().get(0);
		assertThat(permission.getUser()).isSameAs(mockedUser);
		assertThat(permission.getFEntry()).isSameAs(createdFile);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

		verify(observer, times(1)).fEntryChangedNotification(directory, FEntry.ChangeType.ADDED_CHILDREN);    //assert that notification was sent
	}

	@Test
	public void canCreateNewSubDirectories() {
		Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);

		assertThat(createdDirectory.getName()).isEqualTo(TEST_DIRNAME);
		assertThat(directory.getFEntries()).contains(createdDirectory);

		//check permission
		assertThat(createdDirectory.getPermissions()).hasSize(1);
		FEntryPermission permission = createdDirectory.getPermissions().get(0);
		assertThat(permission.getUser()).isSameAs(mockedUser);
		assertThat(permission.getFEntry()).isSameAs(createdDirectory);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

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
