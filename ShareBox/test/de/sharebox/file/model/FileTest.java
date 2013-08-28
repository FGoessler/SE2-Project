package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileTest {
	private File file;

	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private User user;

	@Before
	public void setUp() throws Exception {
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);
		when(user.getEmail()).thenReturn("testmail@test.de");

		file = new File(mockedUserAPI);
	}

	@Test
	public void hasACopyConstructor() {
		file.setName("TestFile");
		file.setIdentifier(1234L);
		file.setPermission(user, true, true, true);

		final File copy = new File(file);

		assertThat(copy).isNotSameAs(file);
		assertThat(copy.getName()).isEqualTo(file.getName());
		assertThat(copy.getIdentifier()).isEqualTo(file.getIdentifier());

		//test that all permission have been deep copied
		assertThat(copy.getPermissions()).isNotEmpty();
		for (final Permission newPermission : copy.getPermissions()) {
			for (final Permission oldPermission : file.getPermissions()) {
				assertThat(newPermission).isNotSameAs(oldPermission);
			}
		}
		//test that all log entries have been deep copied
		assertThat(copy.getLogEntries()).isNotEmpty();
		for (final LogEntry newLogEntry : copy.getLogEntries()) {
			for (final LogEntry oldLogEntry : file.getLogEntries()) {
				assertThat(newLogEntry).isNotSameAs(oldLogEntry);
			}
		}
	}

	@Test
	public void isSubclassOfFEntry() {
		assertThat(file).isInstanceOf(FEntry.class);
	}
}
