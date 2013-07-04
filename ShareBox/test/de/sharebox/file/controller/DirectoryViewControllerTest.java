package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.tree.TreeModel;

import static org.fest.assertions.Assertions.assertThat;

public class DirectoryViewControllerTest {
	private DirectoryViewController controller;
	private Directory rootDirectory;

	@Before
	public void setUp() {
		JTree tree = new JTree();
		new JFrame().add(tree);

		controller = new DirectoryViewController(tree);
	}

	@Before
	public void createMockDirectoryTree() {
		rootDirectory = new Directory();
		rootDirectory.setName("The main dir");

		Directory subDir1 = rootDirectory.createNewDirectory("A Subdirectory");
		subDir1.createNewFile("Subdirectory File");
		rootDirectory.createNewDirectory("Another Subdirectory");

		rootDirectory.createNewFile("A file");
		rootDirectory.createNewFile("Oho!");

		controller.rootDirectory = rootDirectory;
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
}
