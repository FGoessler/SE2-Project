package de.sharebox.file;

import com.google.common.collect.ImmutableList;
import de.sharebox.api.FileAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.FEntryNotification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileManagerTest {
	private Long idCounter = 1L;

	@Mock
	private File file;
	@Mock
	private Directory dir;

	@Mock
	private FileAPI fileAPI;

	@InjectMocks
	private FileManager fileManager;

	@Before
	public void setUp() {
		when(fileAPI.createNewFEntry(any(FEntry.class))).thenReturn(idCounter++);
		when(fileAPI.getChangesSince(anyLong())).thenReturn(ImmutableList.of(file, dir));
		when(file.getIdentifier()).thenReturn(1L);
		when(dir.getIdentifier()).thenReturn(2L);
	}

	@Test
	public void testRegisterFEntry() {
		assertThat(fileManager.registerFEntry(file)).isTrue();
		assertThat(fileManager.registerFEntry(dir)).isTrue();

		verify(file).addObserver(fileManager);
		verify(fileAPI).createNewFEntry(file);
		verify(dir).addObserver(fileManager);
		verify(fileAPI).createNewFEntry(dir);
	}

	@Test
	public void pollAPIForChanges() {
		fileManager.registerFEntry(file);

		fileManager.pollAPIForChanges();

		verify(fileAPI).getChangesSince(anyLong());

		verify(file).applyChangesFromAPI(any(FEntry.class), same(fileManager));
		verify(dir, never()).applyChangesFromAPI(any(FEntry.class), same(fileManager));
	}

	@Test
	public void testPollFileSystemForChanges() {
		assertThat(fileManager.pollFileSystemForChanges()).isTrue();
		//Nothing more to test here since it's not really implemented in this prototype.
	}

	@Test
	public void handlesNotifications() {
		final FEntryNotification updateNotification = new FEntryNotification(file, FEntryNotification.ChangeType.NAME_CHANGED, file);
		fileManager.fEntryNotification(updateNotification);
		verify(fileAPI).updateFEntry(file);

		final FEntryNotification deleteNotification = new FEntryNotification(file, FEntryNotification.ChangeType.DELETED, file);
		fileManager.fEntryNotification(deleteNotification);
		verify(fileAPI).deleteFEntry(file);

		final DirectoryNotification directoryNotification = new DirectoryNotification(dir, FEntryNotification.ChangeType.ADDED_CHILDREN, dir, ImmutableList.<FEntry>of(file));
		fileManager.directoryNotification(directoryNotification);
		verify(fileAPI).updateFEntry(dir);
	}

	@Test
	public void doesNotReactOnNotificationsCausedByItself() {
		final FEntryNotification deleteNotification = new FEntryNotification(file, FEntryNotification.ChangeType.DELETED, fileManager);
		fileManager.fEntryNotification(deleteNotification);
		verify(fileAPI, never()).deleteFEntry(file);

		final DirectoryNotification directoryNotification = new DirectoryNotification(dir, FEntryNotification.ChangeType.ADDED_CHILDREN, fileManager, ImmutableList.<FEntry>of(file));
		fileManager.directoryNotification(directoryNotification);
		verify(fileAPI, never()).updateFEntry(dir);
	}
}
