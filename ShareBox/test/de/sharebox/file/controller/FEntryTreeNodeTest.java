package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FEntryTreeNodeTest {
	private FEntryTreeNode treeNode;

	@Mock
	private DefaultTreeModel treeModel;

	@Test
	public void canBeCreatedWithAFile() {
		File file = new File();
		file.setName("Testfile");
		treeNode = new FEntryTreeNode(treeModel, file);

		assertThat(treeNode.getFEntry()).isSameAs(file);
		assertThat(treeNode.isLeaf()).isTrue();
		assertThat(treeNode.toString()).isEqualTo("Testfile");
		assertThat(treeNode.getChildCount()).isEqualTo(0);
	}

	@Test
	public void canBeCreatedWithADirectory() {
		Directory directory = new Directory();
		directory.setName("Testdir");
		directory.createNewFile("Testfile");
		treeNode = new FEntryTreeNode(treeModel, directory);

		assertThat(treeNode.getFEntry()).isSameAs(directory);
		assertThat(treeNode.isLeaf()).isFalse();
		assertThat(treeNode.getAllowsChildren()).isTrue();
		assertThat(treeNode.toString()).isEqualTo("Testdir");
		assertThat(treeNode.getChildCount()).isEqualTo(1);
	}

	@Test
	public void handlesAddedChildrenNotifications() {
		Directory directory = new Directory();
		treeNode = new FEntryTreeNode(treeModel, directory);
		File addedFile = directory.createNewFile("Testfile");    //this line should fire the notification

		ArgumentCaptor<MutableTreeNode> child = ArgumentCaptor.forClass(MutableTreeNode.class);
		ArgumentCaptor<MutableTreeNode> parent = ArgumentCaptor.forClass(MutableTreeNode.class);
		verify(treeModel).insertNodeInto(child.capture(), parent.capture(), anyInt());

		assertThat(child.getValue()).isInstanceOf(FEntryTreeNode.class);
		assertThat(((FEntryTreeNode) child.getValue()).getFEntry()).isSameAs(addedFile);
		assertThat(parent.getValue()).isInstanceOf(FEntryTreeNode.class);
		assertThat(((FEntryTreeNode) parent.getValue()).getFEntry()).isSameAs(directory);
	}

	@Test
	public void handlesRemovedChildrenNotifications() {
		Directory directory = new Directory();
		File removedFile = directory.createNewFile("Testfile");
		treeNode = new FEntryTreeNode(treeModel, directory);
		directory.deleteFEntry(removedFile);                //this line should fire the notification

		ArgumentCaptor<MutableTreeNode> argument = ArgumentCaptor.forClass(MutableTreeNode.class);
		verify(treeModel).removeNodeFromParent(argument.capture());

		assertThat(argument.getValue()).isInstanceOf(FEntryTreeNode.class);
		assertThat(((FEntryTreeNode) argument.getValue()).getFEntry()).isSameAs(removedFile);
	}
}