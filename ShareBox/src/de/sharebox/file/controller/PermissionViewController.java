package de.sharebox.file.controller;

import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.model.FEntryPermission;
import de.sharebox.file.services.SharingService;
import org.swixml.SwingEngine;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieser Controller ist für die Darstellung der Rechte in der rechten Hälfte des SplitPanes im MainWindow verantwortlich.
 */
public class PermissionViewController {
	private FEntry currentFEntry;

	private DirectoryViewController directoryViewController;

	public JTable permissionTable;
	public JTextArea messageTextArea;
	public JPanel buttonPanel;
	private SharingService sharingService = new SharingService();

	/**
	 * Erstellt einen neuen PermissionViewController.
	 * Dieser Konstruktor kann zum injecten aller vorkommenden Abhängigkeiten verwendet werden. Um eine Instantz mit
	 * Default-Abhängigkeiten zu erhalten ist der public Konstruktor zu verwenden!
	 *
	 * @param splitPane               Der SplitPane in dessen rechter Hälfte der Controller seien Inhalte darstellen soll.
	 * @param directoryViewController Der DirectoryViewController auf den sich diser PermissionViewController bezieht.
	 *                                Wird benötigt um die aktuell ausgewählte Datei/Verzeichnis im JTree zu erhalten.
	 * @param sharingService          Eine SharingService Instanz, die Methoden zum Freigeben von FEntries bereitstellt.
	 */
	protected PermissionViewController(JSplitPane splitPane, DirectoryViewController directoryViewController, SharingService sharingService) {
		try {
			SwingEngine swix = new SwingEngine(this);
			JComponent panel = (JComponent) swix.render("resources/xml/permissionView.xml");
			splitPane.setRightComponent(panel);
		} catch (Exception exception) {
			System.out.println("Couldn't create Permission View!");
		}
		this.sharingService = sharingService;

		this.directoryViewController = directoryViewController;
		this.directoryViewController.addTreeSelectionListener(treeSelectionListener);

		this.permissionTable.setModel(tableModel);
	}

	/**
	 * Erstellt einen neuen PermissionViewController.
	 *
	 * @param splitPane               Der SplitPane in dessen rechter Hälfte der Controller seien Inhalte darstellen soll.
	 * @param directoryViewController Der DirectoryViewController auf den sich diser PermissionViewController bezieht.
	 *                                Wird benötigt um die aktuell ausgewählte Datei/Verzeichnis im JTree zu erhalten.
	 */
	public PermissionViewController(JSplitPane splitPane, DirectoryViewController directoryViewController) {
		this(splitPane, directoryViewController, new SharingService());
	}

	/**
	 * Dieser TreeSelectionListener reagiert auf Änderungen an der Auswahl im JTree des DirectoryViewControllers um den
	 * diesen PermissionViewController zu aktualisieren, wenn der Nutzer einen anderen FEntry auswählt.
	 * Eine Mehrfachauswahl wird dabei nicht unterstützt.
	 */
	protected TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		@Override
		public void valueChanged(TreeSelectionEvent event) {
			if (currentFEntry != null) {
				currentFEntry.removeObserver(fEntryObserver);
			}

			List<FEntry> selectedFEntries = directoryViewController.getSelectedFEntries();
			if (selectedFEntries.size() > 1) {
				currentFEntry = null;

				buttonPanel.setVisible(false);
				messageTextArea.setText("Mehrere Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.");
				messageTextArea.setVisible(true);
			} else if (selectedFEntries.isEmpty()) {
				currentFEntry = null;

				buttonPanel.setVisible(false);
				messageTextArea.setText("Keine Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.");
				messageTextArea.setVisible(true);
			} else {
				buttonPanel.setVisible(true);
				messageTextArea.setVisible(false);

				currentFEntry = selectedFEntries.get(0);
				currentFEntry.addObserver(fEntryObserver);
			}
			tableModel.fireTableDataChanged();
		}
	};

	/**
	 * Dieser FEntryObserver beobachtet den aktuell vom Controller dargestellten FEntry um auf Änderungen am Datenmodel
	 * zu reagieren und das UI zu aktualisieren.
	 */
	protected FEntryObserver fEntryObserver = new FEntryObserver() {
		@Override
		public void fEntryChangedNotification(FEntry fEntry, FEntry.ChangeType reason) {
			if (reason.equals(FEntry.ChangeType.PERMISSION_CHANGED)) {
				tableModel.fireTableDataChanged();
			}
		}

		@Override
		public void fEntryDeletedNotification(FEntry fEntry) {
			currentFEntry = null;

			buttonPanel.setVisible(false);
			messageTextArea.setText("Keine Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.");
			messageTextArea.setVisible(true);

			tableModel.fireTableDataChanged();
		}
	};

	/**
	 * Dieses TableModel ist für die Darstellung der Rechte des im DirectoryViewController selektierten FEntries als
	 * Tabelle verantwortlich.
	 */
	protected AbstractTableModel tableModel = new AbstractTableModel() {
		private String[] columnNames = {"Nutzer", "Lesen", "Schreiben", "Verwalten"};

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			Class<?> columnClass = String.class;
			if (columnIndex > 0) {
				columnClass = Boolean.class;
			}
			return columnClass;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			int rowCount = 0;
			if (currentFEntry != null) {
				rowCount = currentFEntry.getPermissions().size();
			}
			return rowCount;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			FEntryPermission permission = currentFEntry.getPermissions().get(rowIndex);
			Object value;
			switch (columnIndex) {
				case 0:
					value = permission.getUser().getEmail();
					break;
				case 1:
					value = permission.getReadAllowed();
					break;
				case 2:
					value = permission.getWriteAllowed();
					break;
				case 3:
					value = permission.getManageAllowed();
					break;
				default:
					value = "";
					break;
			}
			return value;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			super.setValueAt(aValue, rowIndex, columnIndex);

			FEntryPermission permission = currentFEntry.getPermissions().get(rowIndex);

			switch (columnIndex) {
				case 1:
					permission.setReadAllowed((Boolean) aValue);
					break;
				case 2:
					permission.setWriteAllowed((Boolean) aValue);
					break;
				case 3:
					permission.setManageAllowed((Boolean) aValue);
					break;
				default:
					System.out.println("Unallowed editing!");
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			Boolean editable = false;
			if (columnIndex > 0) {    //can only edit rights - not users
				editable = true;
			}
			return editable;
		}
	};

	/**
	 * Dieser Handler reagiert auf Klicks auf den "+"-Button um andere Nutzer zu der Datei/Verzeichnis einzuladen.
	 * Diese Action wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public Action addUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			sharingService.showShareFEntryDialog(currentFEntry);
		}
	};

	/**
	 * Dieser Handler reagiert auf Klicks auf den "-"-Button um andere Nutzer von der Datei/Verzeichnis auszuladen, also
	 * jegliche Rechte dieser an der Datei zu löschen.
	 * Diese Action wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public Action removeUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			int[] selectedRows = permissionTable.getSelectedRows();
			List<FEntryPermission> selectedPermissions = new ArrayList<FEntryPermission>();
			for (int index : selectedRows) {
				selectedPermissions.add(currentFEntry.getPermissions().get(index));
			}
			for (FEntryPermission permission : selectedPermissions) {
				currentFEntry.setPermission(permission.getUser(), false, false, false);
			}
		}
	};
}
