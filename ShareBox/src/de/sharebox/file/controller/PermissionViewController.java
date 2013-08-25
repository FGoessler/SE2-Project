package de.sharebox.file.controller;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryObserver;
import de.sharebox.file.model.FEntryPermission;
import de.sharebox.file.services.DirectoryViewSelectionService;
import de.sharebox.file.services.SharingService;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieser Rechte-Controller ist für die Darstellung der rechten Hälfte des SplitPanes im MainWindow verantwortlich.
 */
public class PermissionViewController {
	private final DirectoryViewSelectionService selectionService;
	private final SharingService sharingService;
	private final OptionPaneHelper optionPane;

	private Optional<FEntry> currentFEntry = Optional.absent();

	public JTable permissionTable;
	public JTextArea messageTextArea;
	public JPanel buttonPanel;

	/**
	 * Erstellt einen neuen PermissionViewController.<br/>
	 * Sollte nur mittels Dependency Injection durch Guice erstellt werden. Siehe auch PermissionViewControllerFactory.
	 *
	 * @param splitPane        Der SplitPane, in dessen rechter Hälfte der Controller seinen Inhalte darstellen soll.
	 * @param selectionService Ein DirectoryViewSelectionService mittels dessen die aktuelle Auswahl des Nutzer im JTree
	 *                         festgestellt werden kann.
	 * @param sharingService   Eine SharingService-Instanz, die Methoden zum Freigeben von FEntries bereitstellt.
	 */
	@Inject
	PermissionViewController(@Assisted JSplitPane splitPane,
							 DirectoryViewSelectionService selectionService,
							 SharingService sharingService,
							 OptionPaneHelper optionPaneHelper) {

		JComponent panel = (JComponent) new SwingEngineHelper().render(this, "directory/permissionView");
		splitPane.setRightComponent(panel);

		this.optionPane = optionPaneHelper;

		this.sharingService = sharingService;

		this.selectionService = selectionService;
		this.selectionService.addTreeSelectionListener(treeSelectionListener);

		this.permissionTable.setModel(tableModel);
	}

	/**
	 * Der TreeSelectionListener reagiert auf Änderungen an der Auswahl im JTree des DirectoryViewControllers,<br/>
	 * um den PermissionViewController zu aktualisieren, wenn der Nutzer einen anderen FEntry auswählt.<br/>
	 * Eine Mehrfachauswahl wird dabei nicht unterstützt.
	 */
	protected TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		@Override
		public void valueChanged(TreeSelectionEvent event) {
			if (currentFEntry.isPresent()) {
				currentFEntry.get().removeObserver(fEntryObserver);
			}

			List<FEntry> selectedFEntries = selectionService.getSelectedFEntries();
			if (selectedFEntries.size() > 1) {
				currentFEntry = Optional.absent();

				buttonPanel.setVisible(false);
				messageTextArea.setText("Mehrere Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.");
				messageTextArea.setVisible(true);
			} else if (selectedFEntries.isEmpty()) {
				currentFEntry = Optional.absent();

				buttonPanel.setVisible(false);
				messageTextArea.setText("Keine Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.");
				messageTextArea.setVisible(true);
			} else {
				buttonPanel.setVisible(true);
				messageTextArea.setVisible(false);

				currentFEntry = Optional.of(selectedFEntries.get(0));
				currentFEntry.get().addObserver(fEntryObserver);
			}
			tableModel.fireTableDataChanged();
		}
	};

	/**
	 * Der FEntryObserver beobachtet den, aktuell vom Controller, dargestellten FEntry um auf Änderungen am Datenmodel<br/>
	 * zu reagieren und das UI zu aktualisieren.
	 */
	protected FEntryObserver fEntryObserver = new FEntryObserver() {
		@Override
		public void fEntryChangedNotification(FEntry fEntry, ChangeType reason) {
			if (reason.equals(ChangeType.PERMISSION_CHANGED)) {
				tableModel.fireTableDataChanged();
			}
		}

		@Override
		public void fEntryDeletedNotification(FEntry fEntry) {
			currentFEntry = Optional.absent();

			buttonPanel.setVisible(false);
			messageTextArea.setText("Keine Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.");
			messageTextArea.setVisible(true);

			tableModel.fireTableDataChanged();
		}
	};

	/**
	 * Das TableModel stellt die Rechte der selektierten FEntries im DirectoryViewController als Tabelle dar.
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
			if (currentFEntry.isPresent()) {
				rowCount = currentFEntry.get().getPermissions().size();
			}
			return rowCount;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			FEntryPermission permission = currentFEntry.get().getPermissions().get(rowIndex);
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
			if (currentFEntry.get().getPermissionOfCurrentUser().getManageAllowed()) {
				super.setValueAt(aValue, rowIndex, columnIndex);

				FEntryPermission permission = currentFEntry.get().getPermissions().get(rowIndex);

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
			} else {
				optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte für diese Änderung.");
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
	 * Dieser Handler reagiert auf Klicks des "+"-Button, um andere Nutzer zu der Datei/Verzeichnis einzuladen.
	 * Diese Aktion wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public Action addUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			sharingService.showShareFEntryDialog(currentFEntry.get());
		}
	};

	/**
	 * Dieser Handler reagiert auf Klicks des "-"-Button, um andere Nutzer von der Datei/Verzeichnis auszuladen, 
	 * also jegliche Rechte an der Datei zu löschen.
	 * Diese Aktion wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public Action removeUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent event) {
			int[] selectedRows = permissionTable.getSelectedRows();
			List<FEntryPermission> selectedPermissions = new ArrayList<FEntryPermission>();
			for (int index : selectedRows) {
				selectedPermissions.add(currentFEntry.get().getPermissions().get(index));
			}

			if (currentFEntry.get().getPermissionOfCurrentUser().getManageAllowed()) {
				for (FEntryPermission permission : selectedPermissions) {
					currentFEntry.get().setPermission(permission.getUser(), false, false, false);
				}
			} else {
				optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte für diese Änderung.");
			}
		}
	};
}
