package de.sharebox.file.controller;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.LogEntry;
import de.sharebox.file.notification.DirectoryNotification;
import de.sharebox.file.notification.DirectoryObserver;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.helpers.SwingEngineHelper;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class LogViewController {
	private final DirectoryViewSelectionService selectionService;

	private Optional<FEntry> currentFEntry = Optional.absent();

	protected final JComponent panel;
	protected JTable logEntryTable;

	/**
	 * Erstellt einen neuen LogViewController.<br/>
	 * Sollte nur mittel Dependency Injection durch Guice erstellt werden. Siehe auch LogViewControllerFactory.
	 *
	 * @param splitPane        Der SplitPane in dessen linker/oberer Hälfte der Controller seien Inhalte darstellen soll.
	 * @param selectionService Ein DirectoryViewSelectionService mittels dessen die aktuelle Auswahl des Nutzer im JTree
	 *                         festgestellt werden kann.
	 */
	@Inject
	LogViewController(final @Assisted JSplitPane splitPane,
					  final DirectoryViewSelectionService selectionService) {

		panel = (JComponent) new SwingEngineHelper().render(this, "directory/logView");
		splitPane.setTopComponent(panel);

		this.selectionService = selectionService;
		this.selectionService.addTreeSelectionListener(treeSelectionListener);

		this.logEntryTable.setModel(tableModel);
	}

	/**
	 * Dieser TreeSelectionListener reagiert auf Änderungen an der Auswahl im JTree des DirectoryViewControllers um
	 * diesen LogViewController zu aktualisieren, wenn der Nutzer einen anderen FEntry auswählt.
	 * Eine Mehrfachauswahl wird dabei nicht unterstützt.
	 */
	protected TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		@Override
		public void valueChanged(final TreeSelectionEvent event) {
			if (currentFEntry.isPresent()) {
				currentFEntry.get().removeObserver(fEntryObserver);
			}

			final List<FEntry> selectedFEntries = selectionService.getSelectedFEntries();
			if (selectedFEntries.size() == 1) {

				currentFEntry = Optional.of(selectedFEntries.get(0));
				currentFEntry.get().addObserver(fEntryObserver);

				panel.setVisible(true);
			} else {
				currentFEntry = Optional.absent();

				panel.setVisible(false);
			}
			tableModel.fireTableDataChanged();
		}
	};

	/**
	 * Dieser FEntryObserver beobachtet den aktuell vom Controller dargestellten FEntry um auf Änderungen am Datenmodel
	 * zu reagieren und das UI zu aktualisieren.
	 */
	protected DirectoryObserver fEntryObserver = new DirectoryObserver() {
		@Override
		public void fEntryNotification(final FEntryNotification notification) {
			if (notification.getChangeType().equals(FEntryNotification.ChangeType.DELETED)) {
				currentFEntry = Optional.absent();
				panel.setVisible(false);
			}
			tableModel.fireTableDataChanged();
		}

		@Override
		public void directoryNotification(final DirectoryNotification notification) {
			tableModel.fireTableDataChanged();
		}
	};

	/**
	 * Dieses TableModel ist für die Darstellung der LogEntries des im DirectoryViewController selektierten FEntries als
	 * Tabelle verantwortlich.
	 */
	protected AbstractTableModel tableModel = new AbstractTableModel() {
		private final String[] columnNames = {"Datum", "Aktion"};

		@Override
		public String getColumnName(final int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			int rowCount = 0;
			if (currentFEntry.isPresent()) {
				rowCount = currentFEntry.get().getLogEntries().size();
			}
			return rowCount;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final LogEntry logEntry = currentFEntry.get().getLogEntries().get(rowIndex);
			Object value;
			switch (columnIndex) {
				case 0:
					value = logEntry.getDate().toString();
					break;
				case 1:
					value = logEntry.getMessage();
					break;
				default:
					value = "";
					break;
			}
			return value;
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return false;
		}
	};
}
