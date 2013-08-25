package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
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
	private FEntryObserver observer;
	@Mock
	private User user;

	@Before
	public void setUp() {
		fEntry = new FEntry(mockedUserAPI);

		when(user.getEmail()).thenReturn("testmail@test.de");
	}

	@Test
	public void hasACopyConstructor() {
		fEntry.setName("TestFile");
		fEntry.setIdentifier(1234);

		final FEntry copy = new FEntry(fEntry);

		assertThat(copy).isNotSameAs(fEntry);
		assertThat(copy.getName()).isEqualTo(fEntry.getName());
		assertThat(copy.getIdentifier()).isEqualTo(fEntry.getIdentifier());
	}

	@Test
	public void hasAnUniqueID() {
		fEntry.setIdentifier(1234);

		assertThat(fEntry.getIdentifier()).isEqualTo(1234);
	}

	@Test
	public void canRegisterObserversForChangeNotification() {
		fEntry.addObserver(observer);
		fEntry.fireChangeNotification(FEntryObserver.ChangeType.NAME_CHANGED);

		fEntry.removeObserver(observer);
		fEntry.fireChangeNotification(FEntryObserver.ChangeType.NAME_CHANGED);

		//notification should have only been fired once (not fired after removeObserver)
		verify(observer, times(1)).fEntryChangedNotification(fEntry, FEntryObserver.ChangeType.NAME_CHANGED);
	}

	@Test
	public void canRegisterObserversForDeletionNotification() {
		fEntry.addObserver(observer);
		fEntry.fireDeleteNotification();

		fEntry.removeObserver(observer);
		fEntry.fireDeleteNotification();

		//notification should only have been fired once (not fired after removeObserver)
		verify(observer, times(1)).fEntryDeletedNotification(fEntry);
	}

	@Test
	public void firingNotificationsWithoutObserverDoesNotResultInAnError() {
		try {
			fEntry.fireDeleteNotification();
			fEntry.fireChangeNotification(FEntryObserver.ChangeType.NAME_CHANGED);
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

		verify(observer, times(1)).fEntryChangedNotification(fEntry, FEntryObserver.ChangeType.PERMISSION_CHANGED);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final FEntryPermission permission = fEntry.getPermissionOfUser(user);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();
	}

	@Test
	public void settingAPermissionToAllFalseRemovesItFromTheListOfPermissions() {
		fEntry.addObserver(observer);

		fEntry.setPermission(user, true, true, true);
		assertThat(fEntry.getPermissions()).hasSize(1);
		verify(observer, times(1)).fEntryChangedNotification(fEntry, FEntryObserver.ChangeType.PERMISSION_CHANGED);

		fEntry.setPermission(user, false, false, false);

		verify(observer, times(2)).fEntryChangedNotification(fEntry, FEntryObserver.ChangeType.PERMISSION_CHANGED);
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
	}
}
