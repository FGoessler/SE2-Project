package de.sharebox.file.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.File;
import de.sharebox.user.model.User;
import org.junit.Before;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FEntryTreeNodeTest {
	public static final String FILENAME = "Testfile";
	public static final String TESTDIR = "Testdir";
	private FEntryTreeNode treeNode;

	@Mock
	private User user;
	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private DefaultTreeModel treeModel;

	@Before
	public void setUp() {
		when(user.getEmail()).thenReturn("test@test.de");
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);
	}

	@Test
	public void canBeCreatedWithAFile() {
		final File file = new File(mockedUserAPI, FILENAME, user);
		treeNode = new FEntryTreeNode(treeModel, file);

		assertThat(treeNode.getFEntry()).isSameAs(file);
		assertThat(treeNode.isLeaf()).isTrue();
		assertThat(treeNode.toString()).isEqualTo(FILENAME);
		assertThat(treeNode.getChildCount()).isEqualTo(0);
	}

	@Test
	public void canBeCreatedWithADirectory() {
		final Directory directory = new Directory(mockedUserAPI, TESTDIR, user);
		directory.createNewFile(FILENAME);
		treeNode = new FEntryTreeNode(treeModel, directory);

		assertThat(treeNode.getFEntry()).isSameAs(directory);
		assertThat(treeNode.isLeaf()).isFalse();
		assertThat(treeNode.getAllowsChildren()).isTrue();
		assertThat(treeNode.toString()).isEqualTo(TESTDIR);
		assertThat(treeNode.getChildCount()).isEqualTo(1);
	}

	@Test
	public void handlesAddedChildrenNotifications() {
		final Directory directory = new Directory(mockedUserAPI, TESTDIR, user);
		treeNode = new FEntryTreeNode(treeModel, directory);
		final File addedFile = directory.createNewFile(FILENAME).get();    //this line should fire the notification

		final ArgumentCaptor<MutableTreeNode> child = ArgumentCaptor.forClass(MutableTreeNode.class);
		final ArgumentCaptor<MutableTreeNode> parent = ArgumentCaptor.forClass(MutableTreeNode.class);
		verify(treeModel).insertNodeInto(child.capture(), parent.capture(), anyInt());

		assertThat(child.getValue()).isInstanceOf(FEntryTreeNode.class);
		assertThat(((FEntryTreeNode) child.getValue()).getFEntry()).isSameAs(addedFile);
		assertThat(parent.getValue()).isInstanceOf(FEntryTreeNode.class);
		assertThat(((FEntryTreeNode) parent.getValue()).getFEntry()).isSameAs(directory);
	}

	@Test
	public void handlesRemovedChildrenNotifications() {
		final Directory directory = new Directory(mockedUserAPI, TESTDIR, user);
		final File removedFile = directory.createNewFile(FILENAME).get();
		treeNode = new FEntryTreeNode(treeModel, directory);
		directory.deleteFEntry(removedFile);                //this line should fire the notification

		final ArgumentCaptor<MutableTreeNode> argument = ArgumentCaptor.forClass(MutableTreeNode.class);
		verify(treeModel).removeNodeFromParent(argument.capture());

		assertThat(argument.getValue()).isInstanceOf(FEntryTreeNode.class);
		assertThat(((FEntryTreeNode) argument.getValue()).getFEntry()).isSameAs(removedFile);
	}
}
