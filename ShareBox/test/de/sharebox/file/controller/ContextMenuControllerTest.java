package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.file.services.SharingService;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
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
public class ContextMenuControllerTest {
	private ContextMenuController contextMenuController;
	private Directory parentDirectory;
	private TreePath mockedTreePath1;
	private TreePath mockedTreePath2;

	@Mock
	private DirectoryViewController directoryViewController;

	@Before
	public void setUp() {
		contextMenuController = new ContextMenuController(directoryViewController, new DirectoryViewClipboardService());
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
		assertThat(contextMenuController.getSelectedFEntry()).isNull();

		contextMenuController.showMenu(mockedTreePath1, 20, 20);
		assertThat(contextMenuController.getSelectedFEntry()).isSameAs(parentDirectory.getFEntries().get(0));
	}

	@Test
	public void returnsTheSelectedFEntriesParentBasedOnTheGivenTreePath() {
		assertThat(contextMenuController.getSelectedFEntry()).isNull();

		contextMenuController.showMenu(mockedTreePath1, 20, 20);
		assertThat(contextMenuController.getParentOfSelectedFEntry()).isSameAs(parentDirectory);
	}

	@Test
	public void canShowAndHideThePopUpMenu() {
		contextMenuController.showMenu(mockedTreePath1, 20, 20);

		assertThat(contextMenuController.isMenuVisible()).isTrue();
		assertThat(contextMenuController.popupMenu.isVisible()).isTrue();
		assertThat(contextMenuController.getCurrentTreePath()).isEqualTo(mockedTreePath1);

		contextMenuController.hideMenu();

		assertThat(contextMenuController.isMenuVisible()).isFalse();
		assertThat(contextMenuController.popupMenu.isVisible()).isFalse();
		assertThat(contextMenuController.getCurrentTreePath()).isNull();
	}

	@Test
	public void userCanCreateANewFile() {
		performClickOnMenuItem(contextMenuController.createNewFile);

		verify(directoryViewController).createNewFileBasedOnUserSelection();
	}

	@Test
	public void userCanCreateANewDirectory() {
		performClickOnMenuItem(contextMenuController.createNewDirectory);

		verify(directoryViewController).createNewDirectoryBasedOnUserSelection();
	}

	@Test
	public void userCanDeleteAFEntry() {
		assertThat(parentDirectory.getFEntries()).hasSize(2);

		performClickOnMenuItem(contextMenuController.deleteFEntry);

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

		performClickOnMenuItem(contextMenuController.deleteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(0);
	}

	@Test
	public void userCanRenameAFEntry() {
		contextMenuController.optionPane = mock(OptionPaneHelper.class);
		when(contextMenuController.optionPane.showInputDialog(anyString(), anyString())).thenReturn("A new File name");
		assertThat(parentDirectory.getFEntries()).hasSize(2);
		assertThat(parentDirectory.getFEntries().get(0).getName()).isEqualTo("A Test File");

		performClickOnMenuItem(contextMenuController.renameFEntry);

		assertThat(parentDirectory.getFEntries().get(0).getName()).isEqualTo("A new File name");
	}

	@Test
	public void userCanCopyAndPasteAFEntry() {
		assertThat(parentDirectory.getFEntries()).hasSize(2);

		performClickOnMenuItem(contextMenuController.copyFEntry);
		performClickOnMenuItem(contextMenuController.pasteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(3);
	}

	@Test
	public void userCanCopyAndPasteMultipleFEntries() {
		assertThat(parentDirectory.getFEntries()).hasSize(2);

		when(directoryViewController.getSelectedFEntries()).thenReturn(parentDirectory.getFEntries());

		performClickOnMenuItem(contextMenuController.copyFEntry);
		performClickOnMenuItem(contextMenuController.pasteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(4);
	}

	@Test
	public void userCanShareMultipleFEntries() {
		contextMenuController.optionPane = mock(OptionPaneHelper.class);
		contextMenuController.sharingService = mock(SharingService.class);
		when(contextMenuController.optionPane.showInputDialog(anyString(), anyString())).thenReturn("newUser@mail.com");
		when(directoryViewController.getSelectedFEntries()).thenReturn(parentDirectory.getFEntries());

		performClickOnMenuItem(contextMenuController.shareFEntry);

		verify(contextMenuController.sharingService).showShareFEntryDialog(parentDirectory.getFEntries());
	}

	@Test
	public void userCanShareAFEntry() {
		contextMenuController.optionPane = mock(OptionPaneHelper.class);
		contextMenuController.sharingService = mock(SharingService.class);
		when(contextMenuController.optionPane.showInputDialog(anyString(), anyString())).thenReturn("newUser@mail.com");
		List<FEntry> selectionNotIncludingClickPosition = new ArrayList<FEntry>();
		selectionNotIncludingClickPosition.add(parentDirectory.getFEntries().get(1));
		when(directoryViewController.getSelectedFEntries()).thenReturn(selectionNotIncludingClickPosition);

		performClickOnMenuItem(contextMenuController.shareFEntry);

		verify(contextMenuController.sharingService).showShareFEntryDialog(parentDirectory.getFEntries().get(0));
	}

	private void performClickOnMenuItem(Action menuItemAction) {
		contextMenuController.showMenu(mockedTreePath1, 20, 20);

		menuItemAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Action"));
	}
}
