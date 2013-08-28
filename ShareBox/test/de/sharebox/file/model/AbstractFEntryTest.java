package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
import de.sharebox.file.notification.FEntryObserver;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractFEntryTest {
	protected FEntry fEntry;
	@Mock
	protected UserAPI mockedUserAPI;
	@Mock
	protected User user;
	@Mock
	protected FEntryObserver observer;

	@Before
	public void setUp() {
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);
		when(user.getEmail()).thenReturn("testmail@test.de");

		fEntry = new FEntry(mockedUserAPI);
	}
}
