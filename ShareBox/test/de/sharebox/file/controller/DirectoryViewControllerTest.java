package de.sharebox.file.controller;

import de.sharebox.api.FileAPI;
import de.sharebox.api.UserAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryViewControllerTest {

	@Mock
	private User user;
	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private FileAPI fileAPI;

	@Spy
	private JTree tree;
	@Mock
	private ContextMenuController contextMenuController;
	@Mock
	private DirectoryViewSelectionService selectionService;

	private DirectoryViewController controller;

	@Before
	public void setUp() {
		when(user.getRootDirectoryIdentifier()).thenReturn(2000);
		when(mockedUserAPI.getCurrentUser()).thenReturn(user);
		when(fileAPI.getFEntryWithId(anyInt())).thenReturn(new Directory(mockedUserAPI));

		controller = new DirectoryViewController(tree, selectionService, contextMenuController, mockedUserAPI, fileAPI);
	}

	@Test
	public void loadsTheUsersRootDirectoryFromTheFileAPI() {
		verify(fileAPI).getFEntryWithId(2000);
	}

	@Test
	public void hasAJTreeToDrawIn() {
		assertThat(controller.treeView).isNotNull();
	}

	@Test
	public void showsAContextMenuOnRightClick() {
		//simulate click
		when(contextMenuController.isMenuVisible()).thenReturn(false);
		final TreePath treePath = new TreePath(new FEntryTreeNode(mock(DefaultTreeModel.class), new FEntry(mockedUserAPI)));
		when(tree.getPathForLocation(20, 10)).thenReturn(treePath);
		controller.contextMenuMA.mouseReleased(new MouseEvent(controller.treeView, MouseEvent.MOUSE_RELEASED, new Date().getTime(), 0, 20, 10, 1, true, MouseEvent.BUTTON3));

		//validate TreePath
		verify(contextMenuController).showMenu(same(treePath), anyInt(), anyInt());

		//simulate click to hide menu
		when(contextMenuController.isMenuVisible()).thenReturn(true);
		controller.contextMenuMA.mouseReleased(new MouseEvent(controller.treeView, MouseEvent.MOUSE_RELEASED, new Date().getTime(), 0, 20, 10, 1, true, MouseEvent.BUTTON1));

		verify(contextMenuController).hideMenu();
	}
}
