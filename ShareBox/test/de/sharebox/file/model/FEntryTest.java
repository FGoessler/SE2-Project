package de.sharebox.file.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FEntryTest extends AbstractFEntryTestSupport {

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
	public void hasAnUniqueID() {
		fEntry.setIdentifier(1234L);

		assertThat(fEntry.getIdentifier()).isEqualTo(1234L);
	}

	@Test
	public void canAddLogEntriesAndCanBeAskForAListLogEntries() {
		fEntry.addLogEntry(LogEntry.LogMessage.CHANGED);

		assertThat(fEntry.getLogEntries()).hasSize(1);
		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.CHANGED);
	}

	//TODO: test applyAPI changes
}
