package de.sharebox.file.controller;

import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.file.services.SharingService;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissionViewControllerTest {
	private FEntry fEntry;
	@Mock
	private User user1;
	@Mock
	private User user2;

	@Mock
	private FEntryObserver fEntryObserver;
	@Mock
	private TableModelListener tableModelListener;


	@Mock
	private JSplitPane jSplitPane;
	@Mock
	private DirectoryViewSelectionService selectionService;
	@Mock
	private SharingService sharingService;

	@InjectMocks
	private PermissionViewController permissionViewController;

	@Before
	public void setUp() {
		permissionViewController.tableModel.addTableModelListener(tableModelListener);

		//create TestFEntry
		when(user1.getEmail()).thenReturn("user1@mail.com");
		when(user2.getEmail()).thenReturn("user2@mail.com");
		fEntry = new FEntry();
		fEntry.setPermission(user1, true, true, false);
		fEntry.setPermission(user2, true, false, true);
		fEntry.addObserver(fEntryObserver);

		//preselect FEntry
		List<FEntry> selectedFEntries = new ArrayList<FEntry>();
		selectedFEntries.add(fEntry);
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);
		permissionViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));
	}

	@Test
	public void testTheTableModel() {
		//check number of columns
		assertThat(permissionViewController.tableModel.getColumnCount()).isEqualTo(4);

		//check column titles
		assertThat(permissionViewController.tableModel.getColumnClass(0)).isNotNull();
		assertThat(permissionViewController.tableModel.getColumnClass(1)).isNotNull();
		assertThat(permissionViewController.tableModel.getColumnClass(2)).isNotNull();
		assertThat(permissionViewController.tableModel.getColumnClass(3)).isNotNull();

		//check column classes
		assertThat(permissionViewController.tableModel.getColumnClass(0)).isEqualTo(String.class);
		assertThat(permissionViewController.tableModel.getColumnClass(1)).isEqualTo(Boolean.class);
		assertThat(permissionViewController.tableModel.getColumnClass(2)).isEqualTo(Boolean.class);
		assertThat(permissionViewController.tableModel.getColumnClass(3)).isEqualTo(Boolean.class);

		//check column editable
		assertThat(permissionViewController.tableModel.isCellEditable(0, 0)).isFalse();
		assertThat(permissionViewController.tableModel.isCellEditable(0, 1)).isTrue();
		assertThat(permissionViewController.tableModel.isCellEditable(0, 2)).isTrue();
		assertThat(permissionViewController.tableModel.isCellEditable(0, 3)).isTrue();

		//check rows
		assertThat(permissionViewController.tableModel.getRowCount()).isEqualTo(2);
		assertThat(permissionViewController.tableModel.getValueAt(0, 0)).isEqualTo("user1@mail.com");
		assertThat(permissionViewController.tableModel.getValueAt(0, 1)).isEqualTo(Boolean.TRUE);
		assertThat(permissionViewController.tableModel.getValueAt(0, 2)).isEqualTo(Boolean.TRUE);
		assertThat(permissionViewController.tableModel.getValueAt(0, 3)).isEqualTo(Boolean.FALSE);
		assertThat(permissionViewController.tableModel.getValueAt(1, 0)).isEqualTo("user2@mail.com");
		assertThat(permissionViewController.tableModel.getValueAt(1, 1)).isEqualTo(Boolean.TRUE);
		assertThat(permissionViewController.tableModel.getValueAt(1, 2)).isEqualTo(Boolean.FALSE);
		assertThat(permissionViewController.tableModel.getValueAt(1, 3)).isEqualTo(Boolean.TRUE);
	}

	@Test
	public void userCanChangePermissions() {
		assertThat(fEntry.getPermissions().get(0).getManageAllowed()).isFalse();
		assertThat(fEntry.getPermissions().get(1).getReadAllowed()).isTrue();

		permissionViewController.tableModel.setValueAt(true, 0, 3);
		permissionViewController.tableModel.setValueAt(false, 1, 1);

		assertThat(fEntry.getPermissions().get(0).getManageAllowed()).isTrue();
		assertThat(fEntry.getPermissions().get(1).getReadAllowed()).isFalse();

		verify(fEntryObserver, times(2)).fEntryChangedNotification(fEntry, FEntry.ChangeType.PERMISSION_CHANGED);
	}

	@Test
	public void updatesUIOnTreeSelectionChanges() {
		verify(tableModelListener, times(1)).tableChanged(any(TableModelEvent.class));        //one initial invocation already happened


		//no selection -> show message and hide buttons
		List<FEntry> selectedFEntries = new ArrayList<FEntry>();
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);

		permissionViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));

		assertThat(permissionViewController.messageTextArea.isVisible()).isTrue();
		assertThat(permissionViewController.buttonPanel.isVisible()).isFalse();
		verify(tableModelListener, times(2)).tableChanged(any(TableModelEvent.class));


		//single selection -> don't show message and show buttons
		selectedFEntries = new ArrayList<FEntry>();
		selectedFEntries.add(fEntry);
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);

		permissionViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));

		assertThat(permissionViewController.messageTextArea.isVisible()).isFalse();
		assertThat(permissionViewController.buttonPanel.isVisible()).isTrue();
		verify(tableModelListener, times(3)).tableChanged(any(TableModelEvent.class));


		//multi selection -> show message and hide buttons
		selectedFEntries = new ArrayList<FEntry>();
		selectedFEntries.add(fEntry);
		selectedFEntries.add(fEntry);
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);

		permissionViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));

		assertThat(permissionViewController.messageTextArea.isVisible()).isTrue();
		assertThat(permissionViewController.buttonPanel.isVisible()).isFalse();
		verify(tableModelListener, times(4)).tableChanged(any(TableModelEvent.class));
	}

	@Test
	public void updatesUIOnFEntryNotifications() {
		verify(tableModelListener, times(1)).tableChanged(any(TableModelEvent.class));            //one initial invocation already happened
		fEntry.fireChangeNotification(FEntry.ChangeType.NAME_CHANGED);
		verify(tableModelListener, times(1)).tableChanged(any(TableModelEvent.class));

		fEntry.fireChangeNotification(FEntry.ChangeType.PERMISSION_CHANGED);
		verify(tableModelListener, times(2)).tableChanged(any(TableModelEvent.class));

		fEntry.fireDeleteNotification();
		assertThat(permissionViewController.messageTextArea.isVisible()).isTrue();
		assertThat(permissionViewController.buttonPanel.isVisible()).isFalse();
		verify(tableModelListener, times(3)).tableChanged(any(TableModelEvent.class));
	}

	@Test
	public void canInviteUserViaAction() {
		permissionViewController.addUserPermission.actionPerformed(mock(ActionEvent.class));
		verify(sharingService).showShareFEntryDialog(fEntry);
	}

	@Test
	public void canRemoveUserViaAction() {
		permissionViewController.permissionTable.changeSelection(0, 0, false, false);

		permissionViewController.removeUserPermission.actionPerformed(mock(ActionEvent.class));

		assertThat(fEntry.getPermissions()).hasSize(1);
		assertThat(fEntry.getPermissionOfUser(user1).getReadAllowed()).isFalse();
		assertThat(fEntry.getPermissionOfUser(user1).getWriteAllowed()).isFalse();
		assertThat(fEntry.getPermissionOfUser(user1).getManageAllowed()).isFalse();
		assertThat(fEntry.getPermissionOfUser(user2).getReadAllowed()).isTrue();
		assertThat(fEntry.getPermissionOfUser(user2).getWriteAllowed()).isFalse();
		assertThat(fEntry.getPermissionOfUser(user2).getManageAllowed()).isTrue();

		verify(fEntryObserver, times(1)).fEntryChangedNotification(fEntry, FEntry.ChangeType.PERMISSION_CHANGED);
	}
}
