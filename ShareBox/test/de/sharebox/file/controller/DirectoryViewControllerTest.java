package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DirectoryViewControllerTest {
	private DirectoryViewController controller;

	@Before
	public void setUp() {
		JTree tree = new JTree();

		controller = new DirectoryViewController(tree);
	}

	@Before
	public void createMockDirectoryTree() {
		Directory rootDirectory = new Directory();
		rootDirectory.setName("The main dir");

		Directory subDir1 = rootDirectory.createNewDirectory("A Subdirectory");
		subDir1.createNewFile("Subdirectory File");
		rootDirectory.createNewDirectory("Another Subdirectory");

		rootDirectory.createNewFile("A file");
		rootDirectory.createNewFile("Oho!");

		controller.rootDirectory = rootDirectory;
	}

	public void simulateTreeConstruction(DirectoryViewController.TreeNode parent) {
		for(int i = 0; i < controller.getChildCount(parent); i++) {
			DirectoryViewController.TreeNode child = controller.getChild(parent, i);
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
}
