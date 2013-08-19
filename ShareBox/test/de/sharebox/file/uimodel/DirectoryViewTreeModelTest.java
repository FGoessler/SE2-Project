package de.sharebox.file.uimodel;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryViewTreeModelTest {
	private Directory rootDirectory;

	private DirectoryViewTreeModel treeModel;

	@Before
	public void setUp() {
		treeModel = new DirectoryViewTreeModel();
	}

	@Before
	public void createMockDirectoryTree() {
		rootDirectory = new Directory();
		rootDirectory.setName("The main dir");

		Directory subDir = rootDirectory.createNewDirectory("A Subdirectory");
		subDir.createNewFile("Subdirectory File");
		rootDirectory.createNewDirectory("Another Subdirectory");

		rootDirectory.createNewFile("A file");
		rootDirectory.createNewFile("Oho!");

		treeModel.setRootDirectory(rootDirectory);
	}

	public void simulateTreeConstruction(TreeNode parent) {
		for (int i = 0; i < treeModel.getChildCount(parent); i++) {
			TreeNode child = treeModel.getChild(parent, i);
			if (child.getFEntry() instanceof Directory) {
				simulateTreeConstruction(child);
			}
		}
	}

	@Test
	public void isATreeModelAndDeliversContent() {
		assertThat(treeModel).isInstanceOf(TreeModel.class);

		//test getRoot
		Object root = treeModel.getRoot();
		assertThat(root.toString()).isEqualTo("The main dir");

		//test getChild - correct tree!
		Object subDir = treeModel.getChild(root, 0);
		assertThat(subDir.toString()).isEqualTo("A Subdirectory");
		assertThat(treeModel.getChild(subDir, 0).toString()).isEqualTo("Subdirectory File");
		Object subDir2 = treeModel.getChild(root, 1);
		assertThat(subDir2.toString()).isEqualTo("Another Subdirectory");
		assertThat(treeModel.getChild(root, 2).toString()).isEqualTo("A file");
		assertThat(treeModel.getChild(root, 3).toString()).isEqualTo("Oho!");

		//test getChildCount
		assertThat(treeModel.getChildCount(root)).isEqualTo(4);
		assertThat(treeModel.getChildCount(subDir)).isEqualTo(1);
		assertThat(treeModel.getChildCount(subDir2)).isEqualTo(0);

		//test getIndexOfChild
		assertThat(treeModel.getIndexOfChild(root, subDir2)).isEqualTo(1);
		assertThat(treeModel.getIndexOfChild(subDir2, root)).isEqualTo(-1);    //invalid constellations should return -1
		assertThat(treeModel.getIndexOfChild(null, root)).isEqualTo(-1);        //invalid constellations should return -1
	}

	@Test
	public void informsRegisteredTreeModelListenersAboutChanges() {
		TreeModelListener mockedListener = mock(TreeModelListener.class);
		treeModel.addTreeModelListener(mockedListener);

		simulateTreeConstruction(treeModel.getRoot());

		rootDirectory.setName("Changed Name");

		treeModel.removeTreeModelListener(mockedListener);

		rootDirectory.setName("Changed Again");
		verify(mockedListener, times(1)).treeNodesChanged(any(TreeModelEvent.class));        //listener should only have been called while listening!
	}

	@Test
	public void informsRegisteredTreeModelListenersAboutDeletions() {
		TreeModelListener mockedListener = mock(TreeModelListener.class);
		treeModel.addTreeModelListener(mockedListener);

		simulateTreeConstruction(treeModel.getRoot());

		FEntry entryToDelete = rootDirectory.getFEntries().get(1);
		rootDirectory.deleteFEntry(entryToDelete);                                //delete a file

		treeModel.removeTreeModelListener(mockedListener);

		entryToDelete = rootDirectory.getFEntries().get(1);                        //delete another file
		rootDirectory.deleteFEntry(entryToDelete);
		verify(mockedListener, times(1)).treeNodesRemoved(any(TreeModelEvent.class));        //listener should only have been called while listening!
	}

	@Test
	public void informsRegisteredTreeModelListenersAboutInsertions() {
		TreeModelListener mockedListener = mock(TreeModelListener.class);
		treeModel.addTreeModelListener(mockedListener);

		simulateTreeConstruction(treeModel.getRoot());

		rootDirectory.createNewFile("This is a new file!");

		treeModel.removeTreeModelListener(mockedListener);

		rootDirectory.createNewFile("This is a another new file!");
		verify(mockedListener, times(1)).treeNodesInserted(any(TreeModelEvent.class));        //listener should only have been called while listening!
	}

	@Test
	public void canHandleValueForTreePathChangedCalls() {
		TreeNode[] treeNodes = {treeModel.getRoot(), treeModel.getChild(treeModel.getRoot(), 0)};
		TreePath testTreePath = new TreePath(treeNodes);

		treeModel.valueForPathChanged(testTreePath, "A new value");

		assertThat((treeModel.getChild(treeModel.getRoot(), 0)).getFEntry().getName()).isEqualTo("A new value");
	}
}
