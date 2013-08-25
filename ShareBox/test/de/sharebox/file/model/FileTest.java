package de.sharebox.file.model;

import de.sharebox.api.UserAPI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileTest {
	private File file;

	@Mock
	private FEntryObserver observer;
	@Mock
	private UserAPI mockedUserAPI;

	@Before
	public void setUp() throws Exception {
		file = new File(mockedUserAPI);
		file.addObserver(observer);
	}

	@Test
	public void hasACopyConstructor() {
		file.setName("TestFile");
		file.setIdentifier(1234);

		final File copy = new File(file);

		assertThat(copy).isNotSameAs(file);
		assertThat(copy.getName()).isEqualTo(file.getName());
		assertThat(copy.getIdentifier()).isEqualTo(file.getIdentifier());
	}

	@Test
	public void isSubclassOfFEntry() {
		assertThat(file).isInstanceOf(FEntry.class);
	}

	@Test
	public void providesAccessToAFileName() {
		file.setName("testFile");

		assertThat(file.getName()).isEqualTo("testFile");

		verify(observer, times(1)).fEntryChangedNotification(file, FEntryObserver.ChangeType.NAME_CHANGED);            //assert that notification was sent
	}


}
