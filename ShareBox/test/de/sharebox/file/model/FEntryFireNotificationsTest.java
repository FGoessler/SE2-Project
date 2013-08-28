package de.sharebox.file.model;

import de.sharebox.file.notification.FEntryNotification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FEntryFireNotificationsTest extends AbstractFEntryTestSupport {

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
	public void changingTheNameFiresNotificationAndCreatesLogEntry() {
		fEntry.addObserver(observer);

		fEntry.setName("Test");

		final FEntryNotification expectedNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.NAME_CHANGED, fEntry);
		verify(observer).fEntryNotification(expectedNotification);
		assertThat(fEntry.getLogEntries().get(0).getMessage()).isEqualTo(LogEntry.LogMessage.RENAMED);
	}
}
