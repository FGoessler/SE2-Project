package de.sharebox.file.notification;

import com.google.common.collect.ImmutableList;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryNotificationTest {
	@Mock
	private Directory directory;
	@Mock
	private FEntry fEntry;
	private ImmutableList<FEntry> affectedChildren;
	private Object source;

	private DirectoryNotification notification;

	@Before
	public void setUp() {
		source = new Object();
		affectedChildren = ImmutableList.of(fEntry);
		notification = new DirectoryNotification(directory, FEntryNotification.ChangeType.ADDED_CHILDREN, source, affectedChildren);
	}

	@Test
	public void testGetter() {
		assertThat(notification.getChangedFEntry()).isSameAs(directory);
		assertThat(notification.getSource()).isSameAs(source);
		assertThat(notification.getChangeType()).isEqualTo(FEntryNotification.ChangeType.ADDED_CHILDREN);
		assertThat(notification.getAffectedChildren()).isEqualTo(affectedChildren);
	}

	@Test
	public void testEqualsAndHashCode() {
		final FEntryNotification otherNotification = new DirectoryNotification(directory, FEntryNotification.ChangeType.REMOVE_CHILDREN, source, affectedChildren);

		assertThat(notification.equals(otherNotification)).isFalse();
		assertThat(notification.equals(source)).isFalse();
		assertThat(notification.equals(notification)).isTrue();

		assertThat(notification.hashCode()).isNotEqualTo(otherNotification.hashCode());
	}
}
