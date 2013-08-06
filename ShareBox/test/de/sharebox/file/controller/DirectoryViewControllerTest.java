package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryViewControllerTest {
	private DirectoryViewController controller;
	private Directory rootDirectory;
	private Directory subDir1;

	@Before
	public void setUp() {
		JTree tree = new JTree();

		controller = new DirectoryViewController(tree);
	}

	@Before
	public void createMockDirectoryTree() {
		rootDirectory = new Directory();
		rootDirectory.setName("The main dir");

		subDir1 = rootDirectory.createNewDirectory("A Subdirectory");
		subDir1.createNewFile("Subdirectory File");
		rootDirectory.createNewDirectory("Another Subdirectory");

		rootDirectory.createNewFile("A file");
		rootDirectory.createNewFile("Oho!");

		controller.rootDirectory = rootDirectory;
	}

	public void simulateTreeConstruction(TreeNode parent) {
		for(int i = 0; i < controller.getChildCount(parent); i++) {
			TreeNode child = controller.getChild(parent, i);
			if(child.getFEntry() instanceof Directory) {
				simulateTreeConstruction(child);
			}
		}
	}

	@Test
	public void hasAJTreeToDrawIn() {
		assertThat(controller.treeView).isNotNull();
	}

	@Test
	public void isTreeModelAndDeliversContent() {
		assertThat(controller).isInstanceOf(TreeModel.class);
		assertThat(controller.treeView.getModel()).isSameAs(controller);

		//test getRoot
		Object root = controller.getRoot();
		assertThat(root.toString()).isEqualTo("The main dir");

		//test getChild - correct tree!
		Object subDir = controller.getChild(root, 0);
		assertThat(subDir.toString()).isEqualTo("A Subdirectory");
		assertThat(controller.getChild(subDir,0).toString()).isEqualTo("Subdirectory File");
		Object subDir2 = controller.getChild(root, 1);
		assertThat(subDir2.toString()).isEqualTo("Another Subdirectory");
		assertThat(controller.getChild(root,2).toString()).isEqualTo("A file");
		assertThat(controller.getChild(root,3).toString()).isEqualTo("Oho!");

		//test getChildCount
		assertThat(controller.getChildCount(root)).isEqualTo(4);
		assertThat(controller.getChildCount(subDir)).isEqualTo(1);
		assertThat(controller.getChildCount(subDir2)).isEqualTo(0);

		//test getIndexOfChild
		assertThat(controller.getIndexOfChild(root, subDir2)).isEqualTo(1);
		assertThat(controller.getIndexOfChild(subDir2, root)).isEqualTo(-1);	//invalid constellations should return -1
		assertThat(controller.getIndexOfChild(null, root)).isEqualTo(-1);		//invalid constellations should return -1
	}

	@Test
	public void informsRegisteredTreeModelListenersAboutChanges() {
		TreeModelListener mockedListener = mock(TreeModelListener.class);
		controller.addTreeModelListener(mockedListener);

		simulateTreeConstruction(controller.getRoot());

		controller.rootDirectory.setName("Changed Name");

		controller.removeTreeModelListener(mockedListener);

		controller.rootDirectory.setName("Changed Again");
		verify(mockedListener, times(1)).treeNodesChanged(any(TreeModelEvent.class));		//listener should only have been called while listening!
	}

	@Test
	public void informsRegisteredTreeModelListenersAboutDeletions() {
		TreeModelListener mockedListener = mock(TreeModelListener.class);
		controller.addTreeModelListener(mockedListener);

		simulateTreeConstruction(controller.getRoot());

		FEntry entryToDelete = controller.rootDirectory.getFEntries().get(1);
		controller.rootDirectory.deleteFEntry(entryToDelete);								//delete a file

		controller.removeTreeModelListener(mockedListener);

		entryToDelete = controller.rootDirectory.getFEntries().get(1);						//delete another file
		controller.rootDirectory.deleteFEntry(entryToDelete);
		verify(mockedListener, times(1)).treeNodesRemoved(any(TreeModelEvent.class));		//listener should only have been called while listening!
	}

	@Test
	public void informsRegisteredTreeModelListenersAboutInsertions() {
		TreeModelListener mockedListener = mock(TreeModelListener.class);
		controller.addTreeModelListener(mockedListener);

		simulateTreeConstruction(controller.getRoot());

		controller.rootDirectory.createNewFile("This is a new file!");

		controller.removeTreeModelListener(mockedListener);

		controller.rootDirectory.createNewFile("This is a another new file!");
		verify(mockedListener, times(1)).treeNodesInserted(any(TreeModelEvent.class));		//listener should only have been called while listening!
	}

	@Test
	public void canHandleValueForTreePathChangedCalls() {
		TreeNode[] treeNodes = {controller.getRoot(), controller.getChild(controller.getRoot(), 0)};
		TreePath testTreePath = new TreePath(treeNodes);

		controller.valueForPathChanged(testTreePath, "A new value");

		assertThat(controller.getChild(controller.getRoot(), 0).getFEntry().getName()).isEqualTo("A new value");
	}

	@Test
	public void showsAContextMenuOnRightClick() {
		controller.contextMenu = mock(ContextMenu.class);

		//simulate click
		controller.contextMenuMA.mouseReleased(new MouseEvent(controller.treeView, MouseEvent.MOUSE_RELEASED, new Date().getTime(), 0, 20, 10, 1, true, MouseEvent.BUTTON3));

		//validate TreePath
		ArgumentCaptor<TreePath> capturedTreePath = ArgumentCaptor.forClass(TreePath.class);
		verify(controller.contextMenu).showMenu(capturedTreePath.capture(), anyInt(), anyInt());
		Directory selectedDirectory = (Directory)((TreeNode)capturedTreePath.getValue().getLastPathComponent()).getFEntry();
		assertThat(selectedDirectory.getName()).isEqualTo("The main dir");

		//simulate click to hide menu
		controller.contextMenuMA.mouseReleased(new MouseEvent(controller.treeView, MouseEvent.MOUSE_RELEASED, new Date().getTime(), 0, 20, 10, 1, true, MouseEvent.BUTTON1));

		verify(controller.contextMenu).hideMenu();
	}

	@Test
	public void canCreateFilesBasedOnUsersSelection() {
		controller.contextMenu = mock(ContextMenu.class);
		controller.optionPane = mock(OptionPaneHelper.class);
		when(controller.optionPane.showInputDialog(anyString(), anyString())).thenReturn("A new File name!");

		//test without selection -> create as child of root
		when(controller.contextMenu.getSelectedFEntry()).thenReturn(null);
		File newFile = controller.createNewFileBasedOnUserSelection();
		assertThat(rootDirectory.getFEntries()).contains(newFile);
		assertThat(newFile.getName()).isEqualTo("A new File name!");

		//test with directory selected -> create as child of directory
		when(controller.contextMenu.getSelectedFEntry()).thenReturn(subDir1);
		File secondNewFile = controller.createNewFileBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(secondNewFile);
		assertThat(secondNewFile.getName()).isEqualTo("A new File name!");

		//test with file selected -> create as sibling of file
		when(controller.contextMenu.getSelectedFEntry()).thenReturn(secondNewFile);
		when(controller.contextMenu.getParentOfSelectedFEntry()).thenReturn(subDir1);
		File thirdNewFile = controller.createNewFileBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(thirdNewFile);
		assertThat(thirdNewFile.getName()).isEqualTo("A new File name!");

		when(controller.contextMenu.getSelectedFEntry()).thenReturn(null);

		//test with directory selected -> create as child of directory
		TreeNode[] treeNodes = {new TreeNode(rootDirectory), new TreeNode(subDir1)};
		controller.treeView.setSelectionPath(new TreePath(treeNodes));
		File fourthNewFile = controller.createNewFileBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(fourthNewFile);
		assertThat(fourthNewFile.getName()).isEqualTo("A new File name!");

		//test with file selected -> create as sibling of file
		TreeNode[] treeNodes2 = {new TreeNode(rootDirectory), new TreeNode(subDir1), new TreeNode(subDir1.getFEntries().get(0))};
		controller.treeView.setSelectionPath(new TreePath(treeNodes2));
		File fifthNewFile = controller.createNewFileBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(fifthNewFile);
		assertThat(fifthNewFile.getName()).isEqualTo("A new File name!");
	}

	@Test
	public void canCreateDirectoriesBasedOnUsersSelection() {
		controller.contextMenu = mock(ContextMenu.class);
		controller.optionPane = mock(OptionPaneHelper.class);
		when(controller.optionPane.showInputDialog(anyString(), anyString())).thenReturn("A new Directory name!");

		//test without selection -> create as child of root
		when(controller.contextMenu.getSelectedFEntry()).thenReturn(null);
		Directory newDirectory = controller.createNewDirectoryBasedOnUserSelection();
		assertThat(rootDirectory.getFEntries()).contains(newDirectory);
		assertThat(newDirectory.getName()).isEqualTo("A new Directory name!");

		//test with directory right click selected -> create as child of directory
		when(controller.contextMenu.getSelectedFEntry()).thenReturn(subDir1);
		Directory secondNewDirectory = controller.createNewDirectoryBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(secondNewDirectory);
		assertThat(secondNewDirectory.getName()).isEqualTo("A new Directory name!");

		//test with file right click selected -> create as sibling of file
		when(controller.contextMenu.getSelectedFEntry()).thenReturn(subDir1.getFEntries().get(0));
		when(controller.contextMenu.getParentOfSelectedFEntry()).thenReturn(subDir1);
		Directory thirdNewDirectory = controller.createNewDirectoryBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(thirdNewDirectory);
		assertThat(thirdNewDirectory.getName()).isEqualTo("A new Directory name!");

		when(controller.contextMenu.getSelectedFEntry()).thenReturn(null);

		//test with directory selected -> create as child of directory
		TreeNode[] treeNodes = {new TreeNode(rootDirectory), new TreeNode(subDir1)};
		controller.treeView.setSelectionPath(new TreePath(treeNodes));
		Directory fourthNewDirectory = controller.createNewDirectoryBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(fourthNewDirectory);
		assertThat(fourthNewDirectory.getName()).isEqualTo("A new Directory name!");

		//test with file selected -> create as sibling of file
		TreeNode[] treeNodes2 = {new TreeNode(rootDirectory), new TreeNode(subDir1), new TreeNode(subDir1.getFEntries().get(0))};
		controller.treeView.setSelectionPath(new TreePath(treeNodes2));
		Directory fifthNewDirectory = controller.createNewDirectoryBasedOnUserSelection();
		assertThat(subDir1.getFEntries()).contains(fifthNewDirectory);
		assertThat(fifthNewDirectory.getName()).isEqualTo("A new Directory name!");
	}

	@Test
	public void canReturnTheSelectedFEntriesAndTheirParents() {
		TreeNode[] treeNodes = {new TreeNode(rootDirectory), new TreeNode(subDir1)};
		TreePath[] treePaths = {new TreePath(treeNodes), new TreePath(new TreeNode(rootDirectory))};
		controller.treeView.setSelectionPaths(treePaths);

		assertThat(controller.getSelectedFEntries()).contains(rootDirectory, subDir1);

		assertThat(controller.getParentsOfSelectedFEntries()).contains(null, rootDirectory);
	}
}
