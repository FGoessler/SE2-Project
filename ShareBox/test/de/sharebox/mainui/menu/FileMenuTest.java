package de.sharebox.mainui.menu;

import com.google.common.base.Optional;
import de.sharebox.file.controller.DirectoryViewController;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.services.DirectoryViewClipboardService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileMenuTest {
	private FileMenu menu;
	@Mock
	private JMenuBar mockedMenuBar;
	@Mock
	private DirectoryViewController mockedDirectoryViewController;
	@Mock
	private DirectoryViewClipboardService clipboard;

	@Before
	public void setUp() {
		menu = new FileMenu(mockedMenuBar, mockedDirectoryViewController);
		when(mockedDirectoryViewController.getClipboard()).thenReturn(clipboard);
	}

	@Test
	public void creatingMenu() {
		verify(mockedMenuBar).add(any(JMenu.class));
	}

	@Test
	public void testCreateNewFile() {
		menu.createNewFile.actionPerformed(mock(ActionEvent.class));
		verify(mockedDirectoryViewController).createNewFileBasedOnUserSelection();
	}

	@Test
	public void testCreateNewDirectory() {
		menu.createNewDirectory.actionPerformed(mock(ActionEvent.class));
		verify(mockedDirectoryViewController).createNewDirectoryBasedOnUserSelection();
	}

	@Test
	public void testCopyFEntry() {
		List<FEntry> mockedSelectedFEntries = new ArrayList<FEntry>();
		mockedSelectedFEntries.add(new File());
		when(mockedDirectoryViewController.getSelectedFEntries()).thenReturn(mockedSelectedFEntries);

		menu.copyFEntry.actionPerformed(mock(ActionEvent.class));

		verify(clipboard).addToClipboard(mockedSelectedFEntries);
	}

	@Test
	public void testPasteFEntryOntoFile() {
		List<FEntry> mockedSelectedFEntries = new ArrayList<FEntry>();
		mockedSelectedFEntries.add(new File());
		when(mockedDirectoryViewController.getSelectedFEntries()).thenReturn(mockedSelectedFEntries);
		List<Optional<Directory>> mockedSelectedParentDirectories = new ArrayList<Optional<Directory>>();
		mockedSelectedParentDirectories.add(Optional.of(new Directory()));
		when(mockedDirectoryViewController.getParentsOfSelectedFEntries()).thenReturn(mockedSelectedParentDirectories);

		menu.pasteFEntry.actionPerformed(mock(ActionEvent.class));

		verify(clipboard).pasteClipboardContent(mockedSelectedParentDirectories.get(0).get());
	}

	@Test
	public void testPasteFEntryOntoDirectory() {
		List<FEntry> mockedSelectedFEntries = new ArrayList<FEntry>();
		mockedSelectedFEntries.add(new Directory());
		when(mockedDirectoryViewController.getSelectedFEntries()).thenReturn(mockedSelectedFEntries);

		menu.pasteFEntry.actionPerformed(mock(ActionEvent.class));

		verify(clipboard).pasteClipboardContent((Directory)mockedSelectedFEntries.get(0));
	}
}
