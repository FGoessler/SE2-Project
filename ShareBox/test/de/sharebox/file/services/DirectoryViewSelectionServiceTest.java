package de.sharebox.file.services;

import com.google.common.base.Optional;
import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.ContextMenuController;
import de.sharebox.file.controller.FEntryTreeNode;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
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
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPane;
	@Mock
	private ContextMenuController contextMenuController;

	@InjectMocks
	private DirectoryViewSelectionService selectionService;
	private DefaultTreeModel treeModel;

	@Before
	public void setUp() {
		//mock UserAPI (for permissions)
		final User mockedUser = mock(User.class);
		when(mockedUser.getEmail()).thenReturn("test@mail.de");
		when(mockedAPI.getCurrentUser()).thenReturn(mockedUser);

		//create test directories
		rootDirectory = new Directory(mockedAPI);
		rootDirectory.setName("The main dir");
		rootDirectory.setPermission(mockedUser, true, true, true);

		subDir1 = rootDirectory.createNewDirectory("A Subdirectory").get();
		subDir1.createNewFile("Subdirectory File");
		rootDirectory.createNewDirectory("Another Subdirectory");

		rootDirectory.createNewFile("A file");
		rootDirectory.createNewFile("Oho!");

		treeModel = new DefaultTreeModel(null);
		treeModel.setRoot(new FEntryTreeNode(treeModel, rootDirectory));
		selectionService.setTreeView(new JTree(treeModel));
	}

	@Test
	public void canAddTreeSelectionListeners() {
		final TreeSelectionListener treeSelectionListener = mock(TreeSelectionListener.class);
		selectionService.addTreeSelectionListener(treeSelectionListener);

		assertThat(selectionService.getTreeView().getTreeSelectionListeners()).contains(treeSelectionListener);
		selectionService.getTreeView().setSelectionRow(0);
		verify(treeSelectionListener).valueChanged(any(TreeSelectionEvent.class));
	}

	@Test
	public void canCreateFilesBasedOnUsersSelection() {
		//test without selection -> create as child of root
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME + "1");
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());
		final File newFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).get();
		assertThat(rootDirectory.getFEntries()).contains(newFile);
		assertThat(newFile.getName()).isEqualTo(NEW_FILE_NAME + "1");

		//test with directory selected -> create as child of directory
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME + "2");
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1));
		final File secondNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).get();
		assertThat(subDir1.getFEntries()).contains(secondNewFile);
		assertThat(secondNewFile.getName()).isEqualTo(NEW_FILE_NAME + "2");

		//test with file selected -> create as sibling of file
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME + "3");
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(secondNewFile));
		when(contextMenuController.getParentOfSelectedFEntry()).thenReturn(Optional.of(subDir1));
		final File thirdNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).get();
		assertThat(subDir1.getFEntries()).contains(thirdNewFile);
		assertThat(thirdNewFile.getName()).isEqualTo(NEW_FILE_NAME + "3");

		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());

		//test with directory selected (but no context menu) -> create as child of directory
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME + "4");
		final FEntryTreeNode[] treeNodes = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, subDir1)};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes));
		final File fourthNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.<ContextMenuController>absent()).get();
		assertThat(subDir1.getFEntries()).contains(fourthNewFile);
		assertThat(fourthNewFile.getName()).isEqualTo(NEW_FILE_NAME + "4");

		//test with file selected (but no context menu) -> create as sibling of file
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME + "5");
		final FEntryTreeNode[] treeNodes2 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, subDir1), new FEntryTreeNode(treeModel, subDir1.getFEntries().get(0))};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes2));
		final File fifthNewFile = selectionService.createNewFileBasedOnUserSelection(Optional.<ContextMenuController>absent()).get();
		assertThat(subDir1.getFEntries()).contains(fifthNewFile);
		assertThat(fifthNewFile.getName()).isEqualTo(NEW_FILE_NAME + "5");


		//handle dialog cancel
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(null);
		assertThat(selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isFalse();

		//handle invalid file name
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn("");
		assertThat(selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isFalse();

		//handle error from directory when tried to create the same file again
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME);
		assertThat(selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isTrue();
		assertThat(selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isFalse();
		verify(optionPane).showMessageDialog("Eine Datei oder Verzeichnis mit diesem Namen existiert bereits.");
	}

	@Test
	public void canCreateDirectoriesBasedOnUsersSelection() {
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());

		//test without selection -> create as child of root
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME + "1");
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());
		final Directory newDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).get();
		assertThat(rootDirectory.getFEntries()).contains(newDirectory);
		assertThat(newDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME + "1");

		//test with directory right click selected -> create as child of directory
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME + "2");
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1));
		final Directory secondNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).get();
		assertThat(subDir1.getFEntries()).contains(secondNewDirectory);
		assertThat(secondNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME + "2");

		//test with file right click selected -> create as sibling of file
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME + "3");
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1.getFEntries().get(0)));
		when(contextMenuController.getParentOfSelectedFEntry()).thenReturn(Optional.of(subDir1));
		final Directory thirdNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).get();
		assertThat(subDir1.getFEntries()).contains(thirdNewDirectory);
		assertThat(thirdNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME + "3");

		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>absent());

		//test with directory selected (but no context menu) -> create as child of directory
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME + "4");
		final FEntryTreeNode[] treeNodes = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, subDir1)};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes));
		final Directory fourthNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.<ContextMenuController>absent()).get();
		assertThat(subDir1.getFEntries()).contains(fourthNewDirectory);
		assertThat(fourthNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME + "4");

		//test with file selected (but no context menu) -> create as sibling of file
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME + "5");
		final FEntryTreeNode[] treeNodes2 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, subDir1), new FEntryTreeNode(treeModel, subDir1.getFEntries().get(0))};
		selectionService.getTreeView().setSelectionPath(new TreePath(treeNodes2));
		final Directory fifthNewDirectory = selectionService.createNewDirectoryBasedOnUserSelection(Optional.<ContextMenuController>absent()).get();
		assertThat(subDir1.getFEntries()).contains(fifthNewDirectory);
		assertThat(fifthNewDirectory.getName()).isEqualTo(NEW_DIRECTORY_NAME + "5");


		//handle dialog cancel
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(null);
		assertThat(selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isFalse();

		//handle invalid file name
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn("");
		assertThat(selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isFalse();

		//handle error from directory when tried to create the same directory again
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_DIRECTORY_NAME);
		assertThat(selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isTrue();
		assertThat(selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController)).isPresent()).isFalse();
		verify(optionPane).showMessageDialog("Eine Datei oder Verzeichnis mit diesem Namen existiert bereits.");
	}

	@Test
	public void creatingFilesWithoutWritePermissionIsNotPossible() {
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME);
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1));

		setCurrentUserToUserWithoutPermissions();

		assertThat(subDir1.getFEntries()).hasSize(1);
		final Optional<File> newFile = selectionService.createNewFileBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(subDir1.getFEntries()).hasSize(1);
		assertThat(newFile.isPresent()).isFalse();
		verify(optionPane).showMessageDialog("Leider besitzen Sie nicht die nötigen Rechte für diese Operation.");
	}

	@Test
	public void creatingDirectoriesWithoutWritePermissionIsNotPossible() {
		when(optionPane.showInputDialog(anyString(), anyString())).thenReturn(NEW_FILE_NAME);
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.<FEntry>of(subDir1));

		setCurrentUserToUserWithoutPermissions();

		assertThat(subDir1.getFEntries()).hasSize(1);
		final Optional<Directory> newDir = selectionService.createNewDirectoryBasedOnUserSelection(Optional.of(contextMenuController));
		assertThat(subDir1.getFEntries()).hasSize(1);
		assertThat(newDir.isPresent()).isFalse();
		verify(optionPane).showMessageDialog("Leider besitzen Sie nicht die nötigen Rechte für diese Operation.");
	}

	@Test
	public void canReturnTheSelectedFEntriesAndTheirParents() {
		final FEntryTreeNode[] treeNodes = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, subDir1)};
		final TreePath[] treePaths = {new TreePath(treeNodes), new TreePath(new FEntryTreeNode(treeModel, rootDirectory))};
		selectionService.getTreeView().setSelectionPaths(treePaths);

		assertThat(selectionService.getSelectedFEntries()).contains(rootDirectory, subDir1);

		assertThat(selectionService.getParentsOfSelectedFEntries()).contains(Optional.absent(), Optional.of(rootDirectory));
	}

	private void setCurrentUserToUserWithoutPermissions() {
		final User userWithoutPermissions = mock(User.class);
		when(userWithoutPermissions.getEmail()).thenReturn("keine@rechte.de");
		when(mockedAPI.getCurrentUser()).thenReturn(userWithoutPermissions);
	}

	@Test
	public void userCanDeleteAFEntry() {
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.of(rootDirectory.getFEntries().get(0)));
		when(contextMenuController.getParentOfSelectedFEntry()).thenReturn(Optional.of(rootDirectory));

		assertThat(rootDirectory.getFEntries()).hasSize(4);

		selectionService.deleteFEntryBasedOnUserSelection(Optional.of(contextMenuController));

		assertThat(rootDirectory.getFEntries()).hasSize(3);
	}

	@Test
	public void deletingAFEntryWithoutWritePermissionIsNotPossible() {
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.of(rootDirectory.getFEntries().get(0)));
		when(contextMenuController.getParentOfSelectedFEntry()).thenReturn(Optional.of(rootDirectory));
		setCurrentUserToUserWithoutPermissions();

		assertThat(rootDirectory.getFEntries()).hasSize(4);
		selectionService.deleteFEntryBasedOnUserSelection(Optional.of(contextMenuController));

		assertThat(rootDirectory.getFEntries()).hasSize(4);
		verify(optionPane).showMessageDialog(anyString());
	}

	@Test
	public void userCanDeleteMultipleFEntriesAtOnce() {
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.of(rootDirectory.getFEntries().get(0)));
		final FEntryTreeNode[] treeNodes1 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, rootDirectory.getFEntries().get(0))};
		final FEntryTreeNode[] treeNodes2 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, rootDirectory.getFEntries().get(1))};
		final TreePath[] treePaths = {new TreePath(treeNodes1), new TreePath(treeNodes2)};
		selectionService.getTreeView().setSelectionPaths(treePaths);

		assertThat(rootDirectory.getFEntries()).hasSize(4);

		selectionService.deleteFEntryBasedOnUserSelection(Optional.of(contextMenuController));

		assertThat(rootDirectory.getFEntries()).hasSize(2);
	}

	@Test
	public void userCanDeleteMultipleFEntriesAtOnceWithoutContextMenu() {
		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.of(rootDirectory.getFEntries().get(0)));
		final FEntryTreeNode[] treeNodes1 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, rootDirectory.getFEntries().get(0))};
		final FEntryTreeNode[] treeNodes2 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, rootDirectory.getFEntries().get(1))};
		final TreePath[] treePaths = {new TreePath(treeNodes1), new TreePath(treeNodes2)};
		selectionService.getTreeView().setSelectionPaths(treePaths);

		assertThat(rootDirectory.getFEntries()).hasSize(4);

		selectionService.deleteFEntryBasedOnUserSelection(Optional.<ContextMenuController>absent());

		assertThat(rootDirectory.getFEntries()).hasSize(2);
	}

	@Test
	public void deletingMultipleFEntriesWithoutWritePermissionIsNotPossible() {
		setCurrentUserToUserWithoutPermissions();

		when(contextMenuController.getSelectedFEntry()).thenReturn(Optional.of(rootDirectory.getFEntries().get(0)));
		final FEntryTreeNode[] treeNodes1 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, rootDirectory.getFEntries().get(0))};
		final FEntryTreeNode[] treeNodes2 = {new FEntryTreeNode(treeModel, rootDirectory), new FEntryTreeNode(treeModel, rootDirectory.getFEntries().get(1))};
		final TreePath[] treePaths = {new TreePath(treeNodes1), new TreePath(treeNodes2)};
		selectionService.getTreeView().setSelectionPaths(treePaths);

		assertThat(rootDirectory.getFEntries()).hasSize(4);

		selectionService.deleteFEntryBasedOnUserSelection(Optional.of(contextMenuController));

		assertThat(rootDirectory.getFEntries()).hasSize(4);
		//message should contain name of files
		verify(optionPane).showMessageDialog(contains(rootDirectory.getFEntries().get(0).getName()));
		verify(optionPane).showMessageDialog(contains(rootDirectory.getFEntries().get(1).getName()));
	}
}
