package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.notification.FEntryObserver;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FEntryTest {

	private FEntry fEntry;

	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private User user;
	@Mock
	private FEntryObserver observer;

	@Before
	public void setUp() {
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);
		when(user.getEmail()).thenReturn("testmail@test.de");

		fEntry = new FEntry(mockedUserAPI);
	}

	@Test
	public void hasAConstructorToBeCreateWithSpecificValuesAndDefaultPermissions() {
		FEntry testFEntry = new FEntry(mockedUserAPI, "Testfile", user);

		assertThat(testFEntry.getName()).isEqualTo("Testfile");
		assertThat(testFEntry.getPermissionOfUser(user).getReadAllowed()).isTrue();
		assertThat(testFEntry.getPermissionOfUser(user).getWriteAllowed()).isTrue();
		assertThat(testFEntry.getPermissionOfUser(user).getManageAllowed()).isTrue();

		assertThat(testFEntry.getLogEntries()).hasSize(1);
		assertThat(testFEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CREATED);
	}

	@Test
	public void hasACopyConstructor() {
		fEntry.setName("TestFile");
		fEntry.setIdentifier(1234);
		fEntry.setPermission(user, true, true, true);

		final FEntry copy = new FEntry(fEntry);

		assertThat(copy).isNotSameAs(fEntry);
		assertThat(copy.getName()).isEqualTo(fEntry.getName());
		assertThat(copy.getIdentifier()).isEqualTo(fEntry.getIdentifier());

		//test that all permission have been deep copied
		assertThat(copy.getPermissions()).isNotEmpty();
		for (final FEntryPermission newPermission : copy.getPermissions()) {
			for (final FEntryPermission oldPermission : fEntry.getPermissions()) {
				assertThat(newPermission).isNotSameAs(oldPermission);
			}
		}
		//test that all log entries have been deep copied
		assertThat(copy.getLogEntries()).isNotEmpty();
		for (final LogEntry newLogEntry : copy.getLogEntries()) {
			for (final LogEntry oldLogEntry : fEntry.getLogEntries()) {
				assertThat(newLogEntry).isNotSameAs(oldLogEntry);
			}
		}
	}

	@Test
	public void hasAnUniqueID() {
		fEntry.setIdentifier(1234);

		assertThat(fEntry.getIdentifier()).isEqualTo(1234);
	}

	@Test
	public void canRegisterObserversForChangeNotification() {
		fEntry.addObserver(observer);
		fEntry.fireNotification(FEntryNotification.ChangeType.NAME_CHANGED, fEntry);

		fEntry.removeObserver(observer);
		fEntry.fireNotification(FEntryNotification.ChangeType.NAME_CHANGED, fEntry);

		//notification should have only been fired once (not fired after removeObserver)
		final FEntryNotification expectedNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.NAME_CHANGED, fEntry);
		verify(observer, times(1)).fEntryNotification(expectedNotification);
	}

	@Test
	public void canRegisterObserversForDeletionNotification() {
		fEntry.addObserver(observer);
		fEntry.fireNotification(FEntryNotification.ChangeType.DELETED, fEntry);

		fEntry.removeObserver(observer);
		fEntry.fireNotification(FEntryNotification.ChangeType.DELETED, fEntry);

		//notification should only have been fired once (not fired after removeObserver)
		final FEntryNotification expectedNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.DELETED, fEntry);
		verify(observer, times(1)).fEntryNotification(expectedNotification);
	}

	@Test
	public void firingNotificationsWithoutObserverDoesNotResultInAnError() {
		try {
			fEntry.fireNotification(FEntryNotification.ChangeType.DELETED, fEntry);
			fEntry.fireNotification(FEntryNotification.ChangeType.NAME_CHANGED, fEntry);
		} catch (Exception exception) {
			fail("Should not have thrown an error! " + exception.getLocalizedMessage());
		}
	}

	@Test
	public void canBeAskedForAllPermissionsAndThePermissionOfASpecificUserAndThePermissionOfTheCurrentUser() {
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);

		fEntry.setPermission(user, true, true, true);

		assertThat(fEntry.getPermissions()).hasSize(1);
		assertThat(fEntry.getPermissionOfUser(user).getReadAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfUser(user).getWriteAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfUser(user).getManageAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfCurrentUser().getReadAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfCurrentUser().getWriteAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfCurrentUser().getManageAllowed()).isTrue();
	}

	@Test
	public void canSetPermissionsForAUserAndFiresNotification() {
		fEntry.addObserver(observer);

		fEntry.setPermission(user, true, true, true);

		final FEntryNotification expectedNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.PERMISSION_CHANGED, fEntry);
		verify(observer, times(1)).fEntryNotification(expectedNotification);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final FEntryPermission permission = fEntry.getPermissionOfUser(user);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.PERMISSION);
	}

	@Test
	public void settingAPermissionToAllFalseRemovesItFromTheListOfPermissions() {
		fEntry.addObserver(observer);

		fEntry.setPermission(user, true, true, true);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final FEntryNotification expectedNotification1 = new FEntryNotification(fEntry, FEntryNotification.ChangeType.PERMISSION_CHANGED, fEntry);
		verify(observer, times(1)).fEntryNotification(expectedNotification1);

		fEntry.setPermission(user, false, false, false);

		final FEntryNotification expectedNotification2 = new FEntryNotification(fEntry, FEntryNotification.ChangeType.PERMISSION_CHANGED, fEntry);
		verify(observer, times(2)).fEntryNotification(expectedNotification2);
		assertThat(fEntry.getPermissions()).hasSize(0);
		final FEntryPermission permission = fEntry.getPermissionOfUser(user);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isFalse();
		assertThat(permission.getWriteAllowed()).isFalse();
		assertThat(permission.getManageAllowed()).isFalse();
	}

	@Test
	public void changingAPermissionViaSetPermissionDirectlyChangesThePermissionObj() {
		fEntry.setPermission(user, true, true, true);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final FEntryPermission permission = fEntry.getPermissionOfUser(user);

		fEntry.setPermission(user, true, false, false);
		assertThat(fEntry.getPermissions()).hasSize(1);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isFalse();
		assertThat(permission.getManageAllowed()).isFalse();

		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.PERMISSION);
	}

	@Test
	public void changingTheNameFiresNotificationAndCreatesLogEntry() {
		fEntry.addObserver(observer);

		fEntry.setName("Test");

		final FEntryNotification expectedNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.NAME_CHANGED, fEntry);
		verify(observer).fEntryNotification(expectedNotification);
		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.RENAMED);
	}

	@Test
	public void canAddLogEntriesAndCanBeAskForAListLogEntries() {
		fEntry.addLogEntry(LogEntry.LogMessage.CHANGED);

		assertThat(fEntry.getLogEntries()).hasSize(1);
		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CHANGED);
	}
}
