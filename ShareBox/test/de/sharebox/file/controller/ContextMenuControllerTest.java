package de.sharebox.file.controller;

import com.google.common.base.Optional;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.services.DirectoryViewClipboardService;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.file.services.SharingService;
import de.sharebox.file.uimodel.TreeNode;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextMenuControllerTest {
	private Directory parentDirectory;
	private TreePath mockedTreePath1;
	private TreePath mockedTreePath2;

	@Mock
	private DirectoryViewSelectionService selectionService;
	@Mock
	private OptionPaneHelper optionPaneHelper;
	@Mock
	private SharingService sharingService;
	@Spy
	private DirectoryViewClipboardService clipboardService;

	@InjectMocks
	private ContextMenuController contextMenuController;

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
		assertThat(contextMenuController.getSelectedFEntry()).isEqualTo(Optional.absent());

		contextMenuController.showMenu(mockedTreePath1, 20, 20);
		assertThat(contextMenuController.getSelectedFEntry().get()).isSameAs(parentDirectory.getFEntries().get(0));
	}

	@Test
	public void returnsTheSelectedFEntriesParentBasedOnTheGivenTreePath() {
		assertThat(contextMenuController.getSelectedFEntry()).isEqualTo(Optional.absent());

		contextMenuController.showMenu(mockedTreePath1, 20, 20);
		assertThat(contextMenuController.getParentOfSelectedFEntry().get()).isSameAs(parentDirectory);
	}

	@Test
	public void canShowAndHideThePopUpMenu() {
		contextMenuController.showMenu(mockedTreePath1, 20, 20);

		assertThat(contextMenuController.isMenuVisible()).isTrue();
		assertThat(contextMenuController.popupMenu.isVisible()).isTrue();
		assertThat(contextMenuController.getCurrentTreePath().get()).isEqualTo(mockedTreePath1);

		contextMenuController.hideMenu();

		assertThat(contextMenuController.isMenuVisible()).isFalse();
		assertThat(contextMenuController.popupMenu.isVisible()).isFalse();
		assertThat(contextMenuController.getCurrentTreePath()).isEqualTo(Optional.absent());
	}

	@Test
	public void userCanCreateANewFile() {
		performClickOnMenuItem(contextMenuController.createNewFile);

		verify(selectionService).createNewFileBasedOnUserSelection(Optional.of(contextMenuController));
	}

	@Test
	public void userCanCreateANewDirectory() {
		performClickOnMenuItem(contextMenuController.createNewDirectory);

		verify(selectionService).createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController));
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

		when(selectionService.getSelectedFEntries()).thenReturn(parentDirectory.getFEntries());
		List<Optional<Directory>> parents = new ArrayList<Optional<Directory>>();
		parents.add(Optional.of(parentDirectory));
		parents.add(Optional.of(parentDirectory));
		when(selectionService.getParentsOfSelectedFEntries()).thenReturn(parents);

		performClickOnMenuItem(contextMenuController.deleteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(0);
	}

	@Test
	public void userCanRenameAFEntry() {
		when(optionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("A new File name");
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

		when(selectionService.getSelectedFEntries()).thenReturn(parentDirectory.getFEntries());

		performClickOnMenuItem(contextMenuController.copyFEntry);
		performClickOnMenuItem(contextMenuController.pasteFEntry);

		assertThat(parentDirectory.getFEntries()).hasSize(4);
	}

	@Test
	public void userCanShareMultipleFEntries() {
		when(optionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("newUser@mail.com");
		when(selectionService.getSelectedFEntries()).thenReturn(parentDirectory.getFEntries());

		performClickOnMenuItem(contextMenuController.shareFEntry);

		verify(sharingService).showShareFEntryDialog(parentDirectory.getFEntries());
	}

	@Test
	public void userCanShareAFEntry() {
		when(optionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("newUser@mail.com");
		List<FEntry> selectionNotIncludingClickPosition = new ArrayList<FEntry>();
		selectionNotIncludingClickPosition.add(parentDirectory.getFEntries().get(1));
		when(selectionService.getSelectedFEntries()).thenReturn(selectionNotIncludingClickPosition);

		performClickOnMenuItem(contextMenuController.shareFEntry);

		verify(sharingService).showShareFEntryDialog(parentDirectory.getFEntries().get(0));
	}

	private void performClickOnMenuItem(Action menuItemAction) {
		contextMenuController.showMenu(mockedTreePath1, 20, 20);

		menuItemAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Action"));
	}
}
