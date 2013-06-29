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
public class FileTest {
	private File file;

	@Mock
	private FEntryObserver observer;

	@Before
	public void setUp() throws Exception {
		file = new File();
		file.addObserver(observer);
	}

	@Test
	public void isSubclassOfFEntry() {
		assertThat(file).isInstanceOf(FEntry.class);
	}

	@Test
	public void providesAccessToAFileName() {
		file.setFileName("testFile");

		assertThat(file.getFileName()).isEqualTo("testFile");

		verify(observer, times(1)).fEntryChangedNotification(file);         	//assert that notification was sent
	}


}