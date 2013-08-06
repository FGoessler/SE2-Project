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
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContextMenuTest {
	private ContextMenu contextMenu;
	private Directory parentDirectory;
	private TreePath mockedTreePath1;
	private TreePath mockedTreePath2;

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
		FEntry child1 = parentDirectory.createNewFile("A Test File");
		TreeNode[] nodes1 = {new TreeNode(parentDirectory), new TreeNode(child1)};
		mockedTreePath1 = new TreePath(nodes1);
		FEntry child2 = parentDirectory.createNewFile("An other Test File");
		TreeNode[] nodes2 = {new TreeNode(parentDirectory), new TreeNode(child2)};
		mockedTreePath2 = new TreePath(nodes2);
	}

	@Test
	public void returnsTheSelectedFEntryBasedOnTheGivenTreePath() {
		assertThat(contextMenu.getSelectedFEntry()).isNull();

		contextMenu.showMenu(mockedTreePath1, 20, 20);
		assertThat(contextMenu.getSelectedFEntry()).isSameAs(parentDirectory.getFEntries().get(0));
	}

	@Test
	public void returnsTheSelectedFEntriesParentBasedOnTheGivenTreePath() {
		assertThat(contextMenu.getSelectedFEntry()).isNull();

		contextMenu.showMenu(mockedTreePath1, 20, 20);
		assertThat(contextMenu.getParentOfSelectedFEntry()).isSameAs(parentDirectory);
	}

	@Test
	public void canShowAndHideThePopUpMenu() {
		contextMenu.showMenu(mockedTreePath1, 20, 20);

		assertThat(contextMenu.isMenuVisible()).isTrue();
		assertThat(contextMenu.popupMenu.isVisible()).isTrue();
		assertThat(contextMenu.getCurrentTreePath()).isEqualTo(mockedTreePath1);

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
		assertThat(parentDirectory.getFEntries()).hasSize(2);

		performClickOnMenuItem(contextMenu.deleteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(1);
	}

	@Test
	public void userCanDeleteMultipleFEntriesAtOnce() {
		assertThat(parentDirectory.getFEntries()).hasSize(2);

		when(directoryViewController.getSelectedFEntries()).thenReturn(parentDirectory.getFEntries());
		List<Directory> parents = new ArrayList<Directory>();
		parents.add(parentDirectory);
		parents.add(parentDirectory);
		when(directoryViewController.getParentsOfSelectedFEntries()).thenReturn(parents);

		performClickOnMenuItem(contextMenu.deleteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(0);
	}

	@Test
	public void userCanRenameAnFEntry() {
		contextMenu.optionPane = mock(OptionPaneHelper.class);
		when(contextMenu.optionPane.showInputDialog(anyString(), anyString())).thenReturn("A new File name");
		assertThat(parentDirectory.getFEntries()).hasSize(2);
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
		contextMenu.showMenu(mockedTreePath1, 20, 20);

		menuItemAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Action"));
	}
}
