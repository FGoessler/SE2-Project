package de.sharebox.file.model;

import de.sharebox.file.FileManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FEntryTest extends AbstractFEntryTest {

	@Test
	public void hasAConstructorToBeCreatedWithSpecificValuesAndDefaultPermissions() {
		final FEntry testFEntry = new FEntry(mockedUserAPI, "Testfile", user);

		assertThat(testFEntry.getName()).isEqualTo("Testfile");
		assertThat(testFEntry.getPermissionOfUser(user).getReadAllowed()).isTrue();
		assertThat(testFEntry.getPermissionOfUser(user).getWriteAllowed()).isTrue();
		assertThat(testFEntry.getPermissionOfUser(user).getManageAllowed()).isTrue();

		assertThat(testFEntry.getLogEntries()).hasSize(1);
		assertThat(testFEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CREATED);
	}

	@Test
	public void testCopyConstructor() {
		fEntry.setName("TestFile");
		fEntry.setIdentifier(1234L);
		fEntry.setPermission(user, true, true, true);

		final FEntry copy = new FEntry(fEntry);

		assertThat(copy).isNotSameAs(fEntry);
		assertThat(copy.getName()).isEqualTo(fEntry.getName());
		assertThat(copy.getIdentifier()).isEqualTo(fEntry.getIdentifier());

		//test that all permission have been deep copied
		assertThat(copy.getPermissions()).isNotEmpty();
		for (final Permission newPermission : copy.getPermissions()) {
			for (final Permission oldPermission : fEntry.getPermissions()) {
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
	public void testUniqueID() {
		fEntry.setIdentifier(1234L);

		assertThat(fEntry.getIdentifier()).isEqualTo(1234L);
	}

	@Test
	public void testAddLogEntriesAndAskForAListLogEntries() {
		fEntry.addLogEntry(LogEntry.LogMessage.CHANGED);

		assertThat(fEntry.getLogEntries()).hasSize(1);
		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CHANGED);
	}

	@Test
	public void testApplyChangesFormAPI() {
		fEntry.setName("TestFile");
		fEntry.setIdentifier(1234L);
		fEntry.setPermission(user, true, true, true);

		final FEntry updatedFEntry = new FEntry(mockedUserAPI, "newFileName", user);
		updatedFEntry.setPermission(user, true, false, true);

		fEntry.applyChanges(updatedFEntry, mock(FileManager.class));

		assertThat(fEntry).isNotSameAs(updatedFEntry);
		assertThat(fEntry.getName()).isEqualTo("newFileName");
		assertThat(fEntry.getIdentifier()).isEqualTo(1234L);
		assertThat(fEntry.getPermissionOfUser(user).getReadAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfUser(user).getWriteAllowed()).isFalse();
		assertThat(fEntry.getPermissionOfUser(user).getManageAllowed()).isTrue();
	}

}
