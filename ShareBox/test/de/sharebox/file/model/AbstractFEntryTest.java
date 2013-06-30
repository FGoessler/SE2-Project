package de.sharebox.file.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractFEntryTest {

	private transient FEntry fEntry;

	@Mock
	private transient FEntryObserver observer;

	@Before
	public void setUp() {
		fEntry = new FEntry();
	}

	@Test
	public void hasAnUniqueID() {
		fEntry.setIdentifier(1234);

		assertThat(fEntry.getIdentifier()).isEqualTo(1234);
	}

	@Test
	public void canRegisterObserversForChangeNotification() {
		fEntry.addObserver(observer);
		fEntry.fireChangeNotification();

		fEntry.removeObserver(observer);
		fEntry.fireChangeNotification();

		//notification should have only been fired once (not fired after removeObserver)
		verify(observer, times(1)).fEntryChangedNotification(fEntry);
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
		fEntry.fireDeleteNotification();
		fEntry.fireChangeNotification();
	}
}
