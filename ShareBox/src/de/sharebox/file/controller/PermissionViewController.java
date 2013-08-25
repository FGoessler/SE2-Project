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
 * Dieser Controller ist für die Darstellung der Rechte in der rechten Hälfte des SplitPanes im MainWindow verantwortlich.
 */
public class PermissionViewController {
	private final DirectoryViewSelectionService selectionService;
	private final SharingService sharingService;
	private final OptionPaneHelper optionPane;

	private Optional<FEntry> currentFEntry = Optional.absent();

	protected JTable permissionTable;
	protected JTextArea messageTextArea;
	protected JPanel buttonPanel;

	/**
	 * Erstellt einen neuen PermissionViewController.<br/>
	 * Sollte nur mittel Dependency Injection durch Guice erstellt werden. Siehe auch PermissionViewControllerFactory.
	 *
	 * @param splitPane        Der SplitPane in dessen rechter Hälfte der Controller seien Inhalte darstellen soll.
	 * @param selectionService Ein DirectoryViewSelectionService mittels dessen die aktuelle Auswahl des Nutzer im JTree
	 *                         festgestellt werden kann.
	 * @param sharingService   Eine SharingService Instanz, die Methoden zum Freigeben von FEntries bereitstellt.
	 */
	@Inject
	PermissionViewController(final @Assisted JSplitPane splitPane,
							 final DirectoryViewSelectionService selectionService,
							 final SharingService sharingService,
							 final OptionPaneHelper optionPaneHelper) {

		final JComponent panel = (JComponent) new SwingEngineHelper().render(this, "directory/permissionView");
		splitPane.setRightComponent(panel);

		this.optionPane = optionPaneHelper;

		this.sharingService = sharingService;

		this.selectionService = selectionService;
		this.selectionService.addTreeSelectionListener(treeSelectionListener);

		this.permissionTable.setModel(tableModel);
	}

	/**
	 * Dieser TreeSelectionListener reagiert auf Änderungen an der Auswahl im JTree des DirectoryViewControllers um den
	 * diesen PermissionViewController zu aktualisieren, wenn der Nutzer einen anderen FEntry auswählt.
	 * Eine Mehrfachauswahl wird dabei nicht unterstützt.
	 */
	protected TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		@Override
		public void valueChanged(final TreeSelectionEvent event) {
			if (currentFEntry.isPresent()) {
				currentFEntry.get().removeObserver(fEntryObserver);
			}

			final List<FEntry> selectedFEntries = selectionService.getSelectedFEntries();
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
	 * Dieser FEntryObserver beobachtet den aktuell vom Controller dargestellten FEntry um auf Änderungen am Datenmodel
	 * zu reagieren und das UI zu aktualisieren.
	 */
	protected FEntryObserver fEntryObserver = new FEntryObserver() {
		@Override
		public void fEntryChangedNotification(final FEntry fEntry, final ChangeType reason) {
			if (reason.equals(ChangeType.PERMISSION_CHANGED)) {
				tableModel.fireTableDataChanged();
			}
		}

		@Override
		public void fEntryDeletedNotification(final FEntry fEntry) {
			currentFEntry = Optional.absent();

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
		private final String[] columnNames = {"Nutzer", "Lesen", "Schreiben", "Verwalten"};

		@Override
		public String getColumnName(final int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex) {
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
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final FEntryPermission permission = currentFEntry.get().getPermissions().get(rowIndex);
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
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
			if (currentFEntry.get().getPermissionOfCurrentUser().getManageAllowed()) {
				super.setValueAt(aValue, rowIndex, columnIndex);

				final FEntryPermission permission = currentFEntry.get().getPermissions().get(rowIndex);

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
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
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
		public void actionPerformed(final ActionEvent event) {
			sharingService.showShareFEntryDialog(currentFEntry.get());
		}
	};

	/**
	 * Dieser Handler reagiert auf Klicks auf den "-"-Button um andere Nutzer von der Datei/Verzeichnis auszuladen, also
	 * jegliche Rechte dieser an der Datei zu löschen.
	 * Diese Action wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public Action removeUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final int[] selectedRows = permissionTable.getSelectedRows();
			final List<FEntryPermission> selectedPermissions = new ArrayList<FEntryPermission>();
			for (final int index : selectedRows) {
				selectedPermissions.add(currentFEntry.get().getPermissions().get(index));
			}

			if (currentFEntry.get().getPermissionOfCurrentUser().getManageAllowed()) {
				for (final FEntryPermission permission : selectedPermissions) {
					currentFEntry.get().setPermission(permission.getUser(), false, false, false);
				}
			} else {
				optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte für diese Änderung.");
			}
		}
	};
}
