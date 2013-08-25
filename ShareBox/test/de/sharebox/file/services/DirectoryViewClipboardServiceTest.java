package de.sharebox.file.services;

import de.sharebox.api.UserAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryViewClipboardServiceTest {
	private File testFile;
	private Directory testDirectory;
	private Directory targetDirectory;

	private UserAPI mockedAPI;

	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private DirectoryViewClipboardService clipboard;

	@Before
	public void setUp() {
		//mock UserAPI (Permissions)
		final User mockedUser = mock(User.class);
		when(mockedUser.getEmail()).thenReturn("test@mail.de");
		mockedAPI = mock(UserAPI.class);
		when(mockedAPI.getCurrentUser()).thenReturn(mockedUser);

		//create test directory
		testFile = new File(mockedAPI);
		testFile.setName("newFile");

		testDirectory = new Directory(mockedAPI);
		testDirectory.setName("lol");
		testDirectory.setPermission(mockedUser, true, true, true);
		testDirectory.createNewFile("subFile1");
		testDirectory.createNewFile("subFile2");

		targetDirectory = new Directory(mockedAPI);
		targetDirectory.setPermission(mockedUser, true, true, true);
	}

	@Test
	public void canAddFEntriesToClipboardAndPasteThem() {
		clipboard.addToClipboard(testDirectory);
		clipboard.addToClipboard(testFile);

		final File keptFile = targetDirectory.createNewFile("file to be kept").get();
		clipboard.pasteClipboardContent(targetDirectory);

		//check that no file in the target directory got deleted
		assertThat(targetDirectory.getFEntries().get(0)).isEqualTo(keptFile);
		assertThat(targetDirectory.getFEntries().get(0)).isSameAs(keptFile);

		//check that copied objects are deep copies
		assertThat(targetDirectory.getFEntries().get(2).getName()).isEqualTo(testFile.getName());
		assertThat(targetDirectory.getFEntries().get(2)).isNotSameAs(testFile);
		assertThat(targetDirectory.getFEntries().get(1).getName()).isEqualTo(testDirectory.getName());
		final FEntry child = ((Directory) targetDirectory.getFEntries().get(1)).getFEntries().get(0);
		assertThat(child.getName()).isEqualTo(testDirectory.getFEntries().get(0).getName());
		assertThat(targetDirectory.getFEntries().get(1)).isNotSameAs(testDirectory);
	}

	@Test
	public void clipboardOnlyResetsByCallingResetClipboard() {
		clipboard.addToClipboard(testDirectory);

		clipboard.resetClipboard();

		clipboard.addToClipboard(testFile);

		clipboard.pasteClipboardContent(targetDirectory);

		assertThat(targetDirectory.getFEntries()).hasSize(1);
		assertThat(targetDirectory.getFEntries().get(0).getName()).isEqualTo(testFile.getName());
	}

	@Test
	public void canAddAListOfFEntries() {
		final ArrayList<FEntry> fEntries = new ArrayList<FEntry>();
		fEntries.add(testDirectory);
		fEntries.add(testFile);
		clipboard.addToClipboard(fEntries);

		clipboard.pasteClipboardContent(targetDirectory);

		assertThat(targetDirectory.getFEntries().get(1).getName()).isEqualTo(testFile.getName());
		assertThat(targetDirectory.getFEntries().get(0).getName()).isEqualTo(testDirectory.getName());
	}

	@Test
	public void pastingAFEntryToADirectoryWithoutWritePermissionIsNotPossible() {
		setCurrentUserToUserWithoutPermissions();

		assertThat(targetDirectory.getFEntries()).hasSize(0);

		clipboard.addToClipboard(testDirectory);
		clipboard.pasteClipboardContent(targetDirectory);

		assertThat(targetDirectory.getFEntries()).hasSize(0);
		verify(optionPaneHelper).showMessageDialog(anyString());
	}

	private void setCurrentUserToUserWithoutPermissions() {
		final User userWithoutPermissions = mock(User.class);
		when(userWithoutPermissions.getEmail()).thenReturn("keine@rechte.de");
		when(mockedAPI.getCurrentUser()).thenReturn(userWithoutPermissions);
	}
}
