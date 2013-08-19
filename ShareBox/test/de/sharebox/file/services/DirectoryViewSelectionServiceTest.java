package de.sharebox.file.services;

import com.google.common.base.Optional;
import de.sharebox.file.controller.ContextMenuController;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.file.uimodel.DirectoryViewTreeModel;
import de.sharebox.file.uimodel.TreeNode;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryViewSelectionServiceTest {
	public static final String NEW_FILE_NAME = "A new File name!";
	public static final String NEW_DIRECTORY_NAME = "A new Directory name!";

	private Directory rootDirectory;
	private Directory subDir1;

	@Mock
	private OptionPaneHelper optionPane;
	@Mock
	private ContextMenuController contextMenuController;

	@InjectMocks
	private DirectoryViewSelectionService selectionService;

	@Before
	public void createMockDirectoryTree() {
		rootDirectory = new Directory();
		rootDirectory.setName("The main dir");

		subDir1 = rootDirectory.createNewDirectory("A Subdirectory");
		subDir1.createNewFile("Subdirectory File");
		rootDirectory.createNewDirectory("Another Subdirectory");

		rootDirectory.createNewFile("A file");
		rootDirectory.createNewFile("Oho!");

		DirectoryViewTreeModel treeModel = new DirectoryViewTreeModel();
		treeModel.setRootDirectory(rootDirectory);
		selectionService.setTreeView(new JTree(treeModel));
	}


	@Test
	public void canAddTreeSelectionListeners() {
		TreeSelectionListener treeSelectionListener = mock(TreeSelectionListener.class);
		selectionService.addTreeSelectionListener(treeSelectionListener);

		assertThat(selectionService.getTreeView().getTreeSelectionListeners()).contains(treeSelectionListener);
		selectionService.getTreeView().setSelectionRow(0);
		verify(treeSelectionListener).valueChanged(any(TreeSelectionEvent.class));
	}

	@Test
	public void canCreateFilesBasedOnUsersSelection() {
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME);

		//test without selection -> create as child of root
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());
		File newFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(rootDirectory.getFEntries()).contains(newFile);
		assertThat(newFile.getName()).isEqualTo(NEW_FILE_NAME);

		//test with directory selected -> create as child of directory
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1));
		File secondNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(subDir1.getFEntries()).contains(secondNewFile);
		assertThat(secondNewFile.getName()).isEqualTo(NEW_FILE_NAME);

		//test with file selected -> create as sibling of file
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(secondNewFile));
		when(contextMenuController.getParentOfSelectedFEntry()).thenReturn(Optional.of(subDir1));
		File thirdNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(subDir1.getFEntries()).contains(thirdNewFile);
		assertThat(thirdNewFile.getName()).isEqualTo(NEW_FILE_NAME);

		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());

		//test with directory selected -> create as child of directory
		TreeNode[] treeNodes = {new TreeNode(rootDirectory), new TreeNode(subDir1)};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes));
		File fourthNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.<ContextMenuController>absent());
		assertThat(subDir1.getFEntries()).contains(fourthNewFile);
		assertThat(fourthNewFile.getName()).isEqualTo(NEW_FILE_NAME);

		//test with file selected -> create as sibling of file
		TreeNode[] treeNodes2 = {new TreeNode(rootDirectory), new TreeNode(subDir1), new TreeNode(subDir1.getFEntries().get(0))};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes2));
		File fifthNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.<ContextMenuController>absent());
		assertThat(subDir1.getFEntries()).contains(fifthNewFile);
		assertThat(fifthNewFile.getName()).isEqualTo(NEW_FILE_NAME);


		//handle dialog cancel
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(null);
		assertThat(selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController))).isNull();

		//handle invalid file name
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn("");
		assertThat(selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController))).isNull();
	}

	@Test
	public void canCreateDirectoriesBasedOnUsersSelection() {
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME);

		//test without selection -> create as child of root
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());
		Directory newDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(rootDirectory.getFEntries()).contains(newDirectory);
		assertThat(newDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME);

		//test with directory right click selected -> create as child of directory
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1));
		Directory secondNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(subDir1.getFEntries()).contains(secondNewDirectory);
		assertThat(secondNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME);

		//test with file right click selected -> create as sibling of file
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1.getFEntries().get(0)));
		when(contextMenuController.getParentOfSelectedFEntry()).thenReturn(Optional.of(subDir1));
		Directory thirdNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(subDir1.getFEntries()).contains(thirdNewDirectory);
		assertThat(thirdNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME);

		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());

		//test with directory selected -> create as child of directory
		TreeNode[] treeNodes = {new TreeNode(rootDirectory), new TreeNode(subDir1)};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes));
		Directory fourthNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.<ContextMenuController>absent());
		assertThat(subDir1.getFEntries()).contains(fourthNewDirectory);
		assertThat(fourthNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME);

		//test with file selected -> create as sibling of file
		TreeNode[] treeNodes2 = {new TreeNode(rootDirectory), new TreeNode(subDir1), new TreeNode(subDir1.getFEntries().get(0))};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes2));
		Directory fifthNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.<ContextMenuController>absent());
		assertThat(subDir1.getFEntries()).contains(fifthNewDirectory);
		assertThat(fifthNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME);


		//handle dialog cancel
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(null);
		assertThat(selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController))).isNull();

		//handle invalid file name
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn("");
		assertThat(selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController))).isNull();
	}

	@Test
	public void canReturnTheSelectedFEntriesAndTheirParents() {
		TreeNode[] treeNodes = {new TreeNode(rootDirectory), new TreeNode(subDir1)};
		TreePath[] treePaths = {new TreePath(treeNodes), new TreePath(new TreeNode(rootDirectory))};
		selectionService.getTreeView().setSelectionPaths(treePaths);

		assertThat(selectionService.getSelectedFEntries()).contains(rootDirectory, subDir1);

		assertThat(selectionService.getParentsOfSelectedFEntries()).contains(Optional.absent(), Optional.of(rootDirectory));
	}
}
