package de.sharebox.file.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.LogEntry;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.services.DirectoryViewSelectionService;
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
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogViewControllerTest {
	private Directory directory;

	@Mock
	private TableModelListener tableModelListener;

	@Mock
	private JSplitPane jSplitPane;
	@Mock
	private DirectoryViewSelectionService selectionService;

	@InjectMocks
	private LogViewController logViewController;

	@Before
	public void setUp() {
		logViewController.tableModel.addTableModelListener(tableModelListener);

		directory = new Directory(mock(UserAPI.class), "Testdir", mock(User.class));
		directory.addLogEntry(LogEntry.LogMessage.CHANGED);

		//preselect directory
		final List<FEntry> selectedFEntries = new ArrayList<FEntry>();
		selectedFEntries.add(directory);
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);
		logViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));
	}

	@Test
	public void testTheTableModel() {
		//check number of columns
		assertThat(logViewController.tableModel.getColumnCount()).isEqualTo(2);

		//check column titles
		assertThat(logViewController.tableModel.getColumnName(0)).isNotNull();
		assertThat(logViewController.tableModel.getColumnName(1)).isNotNull();

		//check column classes
		assertThat(logViewController.tableModel.getColumnClass(0)).isEqualTo(String.class);
		assertThat(logViewController.tableModel.getColumnClass(1)).isEqualTo(String.class);

		//check column editable
		assertThat(logViewController.tableModel.isCellEditable(0, 0)).isFalse();
		assertThat(logViewController.tableModel.isCellEditable(0, 1)).isFalse();

		//check rows
		assertThat(logViewController.tableModel.getRowCount()).isEqualTo(2);
		assertThat(logViewController.tableModel.getValueAt(0, 0)).isEqualTo(directory.getLogEntries().get(0).getDate().toString());
		assertThat(logViewController.tableModel.getValueAt(0, 1)).isEqualTo(LogEntry.LogMessage.CREATED);
		assertThat(logViewController.tableModel.getValueAt(1, 0)).isEqualTo(directory.getLogEntries().get(1).getDate().toString());
		assertThat(logViewController.tableModel.getValueAt(1, 1)).isEqualTo(LogEntry.LogMessage.CHANGED);
	}

	@Test
	public void changingTheSelectionInTheTreeViewUpdatesTheUI() {
		verify(tableModelListener, times(1)).tableChanged(any(TableModelEvent.class));        //one initial invocation already happened

		//single selection -> show panel
		List<FEntry> selectedFEntries = new ArrayList<FEntry>();
		selectedFEntries.add(directory);
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);

		logViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));

		assertThat(logViewController.panel.isVisible()).isTrue();
		verify(tableModelListener, times(2)).tableChanged(any(TableModelEvent.class));


		//multi selection -> hide panel
		selectedFEntries = new ArrayList<FEntry>();
		selectedFEntries.add(directory);
		selectedFEntries.add(directory);
		when(selectionService.getSelectedFEntries()).thenReturn(selectedFEntries);

		logViewController.treeSelectionListener.valueChanged(mock(TreeSelectionEvent.class));

		assertThat(logViewController.panel.isVisible()).isFalse();
		verify(tableModelListener, times(3)).tableChanged(any(TableModelEvent.class));
	}

	@Test
	public void notificationsUpdateTheUI() {
		verify(tableModelListener, times(1)).tableChanged(any(TableModelEvent.class));            //one initial invocation already happened

		directory.fireNotification(FEntryNotification.ChangeType.NAME_CHANGED, directory);
		verify(tableModelListener, times(2)).tableChanged(any(TableModelEvent.class));

		directory.fireNotification(FEntryNotification.ChangeType.PERMISSION_CHANGED, directory);
		verify(tableModelListener, times(3)).tableChanged(any(TableModelEvent.class));

		directory.fireDirectoryNotification(FEntryNotification.ChangeType.ADDED_CHILDREN, mock(FEntry.class), directory);
		verify(tableModelListener, times(4)).tableChanged(any(TableModelEvent.class));

		directory.fireDirectoryNotification(FEntryNotification.ChangeType.REMOVE_CHILDREN, mock(FEntry.class), directory);
		verify(tableModelListener, times(5)).tableChanged(any(TableModelEvent.class));

		directory.fireNotification(FEntryNotification.ChangeType.DELETED, directory);
		assertThat(logViewController.panel.isVisible()).isFalse();
		verify(tableModelListener, times(6)).tableChanged(any(TableModelEvent.class));

	}
}
