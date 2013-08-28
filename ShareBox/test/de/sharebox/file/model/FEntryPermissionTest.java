package de.sharebox.file.model;

import de.sharebox.file.notification.FEntryNotification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FEntryPermissionTest extends AbstractFEntryTest {

	@Test
	public void testAskForPermissions() {
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
	public void testSetPermissionsForAUserFiresNotification() {
		fEntry.addObserver(observer);

		fEntry.setPermission(user, true, true, true);

		final FEntryNotification expectedNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.PERMISSION_CHANGED, fEntry);
		verify(observer, times(1)).fEntryNotification(expectedNotification);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final Permission permission = fEntry.getPermissionOfUser(user);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.PERMISSION);
	}

	@Test
	public void testSettingAPermissionToAllFalseRemovesItFromTheListOfPermissions() {
		fEntry.addObserver(observer);

		fEntry.setPermission(user, true, true, true);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final FEntryNotification expectedNotification1 = new FEntryNotification(fEntry, FEntryNotification.ChangeType.PERMISSION_CHANGED, fEntry);
		verify(observer, times(1)).fEntryNotification(expectedNotification1);

		fEntry.setPermission(user, false, false, false);

		final FEntryNotification expectedNotification2 = new FEntryNotification(fEntry, FEntryNotification.ChangeType.PERMISSION_CHANGED, fEntry);
		verify(observer, times(2)).fEntryNotification(expectedNotification2);
		assertThat(fEntry.getPermissions()).hasSize(0);
		final Permission permission = fEntry.getPermissionOfUser(user);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isFalse();
		assertThat(permission.getWriteAllowed()).isFalse();
		assertThat(permission.getManageAllowed()).isFalse();
	}

	@Test
	public void testChangingAPermissionViaSetPermissionDirectlyChangesThePermissionObj() {
		fEntry.setPermission(user, true, true, true);
		assertThat(fEntry.getPermissions()).hasSize(1);
		final Permission permission = fEntry.getPermissionOfUser(user);

		fEntry.setPermission(user, true, false, false);
		assertThat(fEntry.getPermissions()).hasSize(1);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isFalse();
		assertThat(permission.getManageAllowed()).isFalse();

		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.PERMISSION);
	}
}
