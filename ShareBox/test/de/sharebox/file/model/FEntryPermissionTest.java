package de.sharebox.file.model;

import de.sharebox.user.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FEntryPermissionTest {

	@Mock
	private User user;
	@Mock
	private FEntry fEntry;

	@Test
	public void testCreationAndSetterAndGetter() {
		FEntryPermission permission = new FEntryPermission(user, fEntry);

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
	}
}
