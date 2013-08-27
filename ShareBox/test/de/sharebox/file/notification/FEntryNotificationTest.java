package de.sharebox.file.notification;

import de.sharebox.file.model.FEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FEntryNotificationTest {
	@Mock
	private FEntry fEntry;
	private Object source;

	private FEntryNotification notification;

	@Before
	public void setUp() {
		source = new Object();
		notification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.NAME_CHANGED, source);
	}

	@Test
	public void testGetter() {
		assertThat(notification.getChangedFEntry()).isSameAs(fEntry);
		assertThat(notification.getSource()).isSameAs(source);
		assertThat(notification.getChangeType()).isEqualTo(FEntryNotification.ChangeType.NAME_CHANGED);
	}

	@Test
	public void testEqualsAndHashCode() {
		final FEntryNotification otherNotification = new FEntryNotification(fEntry, FEntryNotification.ChangeType.DELETED, source);

		assertThat(notification.equals(otherNotification)).isFalse();
		assertThat(notification.equals(source)).isFalse();
		assertThat(notification.equals(notification)).isTrue();

		assertThat(notification.hashCode()).isNotEqualTo(otherNotification.hashCode());
	}
}
