package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContextMenuTest {
	private ContextMenu contextMenu;
	private TreePath mockedTreePath;
	private Directory parentDirectory;

	@Mock
	private DirectoryViewController directoryViewController;

	@Before
	public void setUp() {
		contextMenu = new ContextMenu(directoryViewController);
	}

	@Before
	public void createMockedTreePath() {
		parentDirectory = new Directory();
		parentDirectory.setName("A Test Dir");
		FEntry child = parentDirectory.createNewFile("A Test File");
		TreeNode[] nodes = {new TreeNode(parentDirectory), new TreeNode(child)};
		mockedTreePath = new TreePath(nodes);
	}

	@Test
	public void returnsTheSelectedFEntryBasedOnTheGivenTreePath() {
		assertThat(contextMenu.getSelectedFEntry()).isNull();

		contextMenu.showMenu(mockedTreePath, 20, 20);
		assertThat(contextMenu.getSelectedFEntry()).isSameAs(parentDirectory.getFEntries().get(0));
	}

	@Test
	public void returnsTheSelectedFEntriesParentBasedOnTheGivenTreePath() {
		assertThat(contextMenu.getSelectedFEntry()).isNull();

		contextMenu.showMenu(mockedTreePath, 20, 20);
		assertThat(contextMenu.getParentOfSelectedFEntry()).isSameAs(parentDirectory);
	}

	@Test
	public void canShowAndHideThePopUpMenu() {
		contextMenu.showMenu(mockedTreePath, 20, 20);

		assertThat(contextMenu.isMenuVisible()).isTrue();
		assertThat(contextMenu.popupMenu.isVisible()).isTrue();
		assertThat(contextMenu.getCurrentTreePath()).isEqualTo(mockedTreePath);

		contextMenu.hideMenu();

		assertThat(contextMenu.isMenuVisible()).isFalse();
		assertThat(contextMenu.popupMenu.isVisible()).isFalse();
		assertThat(contextMenu.getCurrentTreePath()).isNull();
	}

	@Test
	public void userCanCreateANewFile() {
		performClickOnMenuItem(contextMenu.createNewFile);

		verify(directoryViewController).createNewFileBasedOnUserSelection();
	}

	@Test
	public void userCanCreateANewDirectory() {
		performClickOnMenuItem(contextMenu.createNewDirectory);

		verify(directoryViewController).createNewDirectoryBasedOnUserSelection();
	}

	@Test
	public void userCanDeleteAnFEntry() {
		assertThat(parentDirectory.getFEntries()).hasSize(1);

		performClickOnMenuItem(contextMenu.deleteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(0);
	}

	@Test
	public void userCanRenameAnFEntry() {
		contextMenu.optionPane = mock(OptionPaneHelper.class);
		when(contextMenu.optionPane.showInputDialog(anyString(), anyString())).thenReturn("A new File name");
		assertThat(parentDirectory.getFEntries()).hasSize(1);
		assertThat(parentDirectory.getFEntries().get(0).getName()).isEqualTo("A Test File");

		performClickOnMenuItem(contextMenu.renameFEntry);

		assertThat(parentDirectory.getFEntries().get(0).getName()).isEqualTo("A new File name");
	}

	@Ignore
	@Test
	public void userCanCopyAnFEntry() {
		//TODO: implement and test copy FEntries via ContextMenu!
		contextMenu.copyFEntry.actionPerformed(mock(ActionEvent.class));
	}

	@Ignore
	@Test
	public void userCanPasteAnFEntry() {
		//TODO: implement and test paste FEntries via ContextMenu!
		contextMenu.pasteFEntry.actionPerformed(mock(ActionEvent.class));
	}

	@Ignore
	@Test
	public void userCanShareAnFEntry() {
		//TODO: implement and test share FEntries via ContextMenu!
		contextMenu.shareFEntry.actionPerformed(mock(ActionEvent.class));
	}

	private void performClickOnMenuItem(Action menuItemAction) {
		contextMenu.showMenu(mockedTreePath, 20, 20);

		menuItemAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Action"));
	}
}
