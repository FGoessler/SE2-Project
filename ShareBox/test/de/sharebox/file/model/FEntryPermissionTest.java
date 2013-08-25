package de.sharebox.file.model;

import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FEntryPermissionTest {

	@Mock
	private User user;
	@Mock
	private FEntry fEntry;

	private FEntryPermission permission;

	@Before
	public void setUp() {
		permission = new FEntryPermission(user, fEntry);
	}

	@Test
	public void testCreationAndSetterAndGetter() {
		assertThat(permission.getUser()).isSameAs(user);
		assertThat(permission.getFEntry()).isSameAs(fEntry);
		assertThat(permission.getReadAllowed()).isFalse();
		assertThat(permission.getWriteAllowed()).isFalse();
		assertThat(permission.getManageAllowed()).isFalse();

		permission.setReadAllowed(true);
		permission.setWriteAllowed(true);
		permission.setManageAllowed(true);

		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isTrue();

		permission.setPermissions(false, false, false);

		assertThat(permission.getReadAllowed()).isFalse();
		assertThat(permission.getWriteAllowed()).isFalse();
		assertThat(permission.getManageAllowed()).isFalse();
	}

	@Test
	public void settingValuesFiresEventOnFEntry() {
		permission.setReadAllowed(true);
		verify(fEntry, times(1)).fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);

		permission.setWriteAllowed(true);
		verify(fEntry, times(2)).fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);

		permission.setManageAllowed(true);
		verify(fEntry, times(3)).fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);

		permission.setPermissions(false, false, false);
		verify(fEntry, times(4)).fireChangeNotification(FEntryObserver.ChangeType.PERMISSION_CHANGED);
	}
}
