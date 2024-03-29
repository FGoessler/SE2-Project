package de.sharebox.mainui.menu;

import com.google.common.base.Optional;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.ContextMenuController;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.file.services.DirectoryViewSelectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

	@Mock
	private UserAPI mockedUserAPI;

	@Mock
	private JMenuBar mockedMenuBar;
	@Mock
	private DirectoryViewSelectionService selectionService;
	@Mock
	private DirectoryViewClipboardService clipboard;

	@InjectMocks
	private FileMenu menu;

	@Test
	public void creatingMenu() {
		verify(mockedMenuBar).add(any(JMenu.class));
	}

	@Test
	public void testCreateNewFile() {
		menu.createNewFile.actionPerformed(mock(ActionEvent.class));
		verify(selectionService).createNewFileBasedOnUserSelection(Optional.<ContextMenuController>absent());
	}

	@Test
	public void testCreateNewDirectory() {
		menu.createNewDirectory.actionPerformed(mock(ActionEvent.class));
		verify(selectionService).createNewDirectoryBasedOnUserSelection(Optional.<ContextMenuController>absent());
	}

	@Test
	public void testCopyFEntry() {
		final List<FEntry> mockedSelectedFEntries = new ArrayList<FEntry>();
		mockedSelectedFEntries.add(new File(mockedUserAPI));
		when(selectionService.getSelectedFEntries()).thenReturn(mockedSelectedFEntries);

		menu.copyFEntry.actionPerformed(mock(ActionEvent.class));

		verify(clipboard).addToClipboard(mockedSelectedFEntries);
	}

	@Test
	public void testPasteFEntryOntoFile() {
		final List<FEntry> mockedSelectedFEntries = new ArrayList<FEntry>();
		mockedSelectedFEntries.add(new File(mockedUserAPI));
		when(selectionService.getSelectedFEntries()).thenReturn(mockedSelectedFEntries);
		final List<Optional<Directory>> mockedSelectedParentDirectories = new ArrayList<Optional<Directory>>();
		mockedSelectedParentDirectories.add(Optional.of(new Directory(mockedUserAPI)));
		when(selectionService.getParentsOfSelectedFEntries()).thenReturn(mockedSelectedParentDirectories);

		menu.pasteFEntry.actionPerformed(mock(ActionEvent.class));

		verify(clipboard).pasteClipboardContent(mockedSelectedParentDirectories.get(0).get());
	}

	@Test
	public void testPasteFEntryOntoDirectory() {
		final List<FEntry> mockedSelectedFEntries = new ArrayList<FEntry>();
		mockedSelectedFEntries.add(new Directory(mockedUserAPI));
		when(selectionService.getSelectedFEntries()).thenReturn(mockedSelectedFEntries);

		menu.pasteFEntry.actionPerformed(mock(ActionEvent.class));

		verify(clipboard).pasteClipboardContent((Directory) mockedSelectedFEntries.get(0));
	}
}
