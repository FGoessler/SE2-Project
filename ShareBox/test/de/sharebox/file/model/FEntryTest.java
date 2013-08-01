package de.sharebox.file.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FEntryTest {

	private transient FEntry fEntry;

	@Mock
	private transient FEntryObserver observer;

	@Before
	public void setUp() {
		fEntry = new FEntry();
	}

	@Test
	public void hasACopyConstructor() {
		fEntry.setName("TestFile");
		fEntry.setIdentifier(1234);

		FEntry copy = new FEntry(fEntry);

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
		fEntry.fireChangeNotification(FEntry.ChangeType.NAME_CHANGED);

		fEntry.removeObserver(observer);
		fEntry.fireChangeNotification(FEntry.ChangeType.NAME_CHANGED);

		//notification should have only been fired once (not fired after removeObserver)
		verify(observer, times(1)).fEntryChangedNotification(fEntry, FEntry.ChangeType.NAME_CHANGED);
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
			fEntry.fireChangeNotification(FEntry.ChangeType.NAME_CHANGED);
		} catch(Exception exception) {
			fail("Should not have thrown an error! " + exception.getLocalizedMessage());
		}
	}
}
