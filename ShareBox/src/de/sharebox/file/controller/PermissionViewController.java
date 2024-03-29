package de.sharebox.file.controller;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.sharebox.api.UserAPI;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.Permission;
import de.sharebox.file.notification.FEntryNotification;
import de.sharebox.file.notification.FEntryObserver;
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
 * Dieser Controller ist für die Darstellung der Rechte, in der rechten Hälfte des SplitPanes, im MainWindow verantwortlich.
 */
public class PermissionViewController {
	public static final String NO_FENTRY_SELECTED_MSG = "Keine Dateien/ Verzeichnisse angewählt - keine Detailinformationen verfügbar.";

	private final DirectoryViewSelectionService selectionService;
	private final SharingService sharingService;
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private Optional<FEntry> currentFEntry = Optional.absent();

	protected JTable permissionTable;
	protected JTextArea messageTextArea;
	protected JPanel buttonPanel;

	/**
	 * Erstellt einen neuen PermissionViewController.<br/>
	 * Sollte nur mittels Dependency Injection durch Guice erstellt werden. Siehe auch PermissionViewControllerFactory.
	 *
	 * @param splitPane        Der SplitPane, in dessen rechter Hälfte der Controller seinen Inhalte darstellen soll.
	 * @param selectionService Ein DirectoryViewSelectionService, mittels dessen die aktuelle Auswahl des Nutzer im JTree
	 *                         festgestellt werden kann.
	 * @param sharingService   Eine SharingService-Instanz, die Methoden zum Freigeben von FEntries bereitstellt.
	 * @param optionPaneHelper Ein OptionPaneHelper zum Erstellen von Dialogfenstern.
	 * @param userAPI          Die UserAPI. Wird hier benötigt um den aktuell eingeloggten Benutzer festzustellen.
	 */
	@Inject
	PermissionViewController(final @Assisted JSplitPane splitPane,
							 final DirectoryViewSelectionService selectionService,
							 final SharingService sharingService,
							 final OptionPaneHelper optionPaneHelper,
							 final UserAPI userAPI) {

		final JComponent panel = (JComponent) new SwingEngineHelper().render(this, "directory/permissionView");
		splitPane.setRightComponent(panel);

		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;

		this.sharingService = sharingService;

		this.selectionService = selectionService;
		this.selectionService.addTreeSelectionListener(treeSelectionListener);

		this.permissionTable.setModel(tableModel);
	}

	/**
	 * Der TreeSelectionListener reagiert auf Änderungen an der Auswahl im JTree des DirectoryViewControllers,
	 * um den PermissionViewController zu aktualisieren, wenn der Nutzer einen anderen FEntry auswählt.<br/>
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
				messageTextArea.setText(NO_FENTRY_SELECTED_MSG);
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
	 * Der FEntryObserver beobachtet den, aktuell vom Controller, dargestellten FEntry, um auf Änderungen am Datenmodel
	 * zu reagieren und das UI zu aktualisieren.
	 */
	protected FEntryObserver fEntryObserver = new FEntryObserver() {
		@Override
		public void fEntryNotification(final FEntryNotification notification) {
			if (notification.getChangeType().equals(FEntryNotification.ChangeType.PERMISSION_CHANGED)) {
				tableModel.fireTableDataChanged();
			} else if (notification.getChangeType().equals(FEntryNotification.ChangeType.DELETED)) {
				currentFEntry = Optional.absent();

				buttonPanel.setVisible(false);
				messageTextArea.setText(NO_FENTRY_SELECTED_MSG);
				messageTextArea.setVisible(true);

				tableModel.fireTableDataChanged();
			}
		}
	};

	/**
	 * Das TableModel stellt die Rechte des selektierten FEntries, im DirectoryViewController, als Tabelle dar.
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
			return columnNames.length;
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
			final Permission permission = currentFEntry.get().getPermissions().get(rowIndex);
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

				final Permission permission = currentFEntry.get().getPermissions().get(rowIndex);

				switch (columnIndex) {
					case 1:
						if (showOwnPermissionWarning(permission)) {
							permission.setReadAllowed((Boolean) aValue);
						}
						break;
					case 2:
						if (showOwnPermissionWarning(permission)) {
							permission.setWriteAllowed((Boolean) aValue);
						}
						break;
					case 3:
						if (showOwnPermissionWarning(permission)) {
							permission.setManageAllowed((Boolean) aValue);
						}
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
	 * Dieser Handler reagiert auf Klicks des "+"-Button, um andere Nutzer zu der Datei/Verzeichnis einzuladen.<br/>
	 * Diese Aktion wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public final Action addUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			sharingService.showShareFEntryDialog(currentFEntry.get());
		}
	};

	/**
	 * Dieser Handler reagiert auf Klicks des "-"-Button, um andere Nutzer von der Datei/Verzeichnis auszuladen,
	 * also jegliche Rechte an der Datei zu löschen.<br/>
	 * Diese Aktion wird per SWIxml automatisch mit dem UI verknüpft.
	 */
	public final Action removeUserPermission = new AbstractAction() {
		@Override
		public void actionPerformed(final ActionEvent event) {
			final int[] selectedRows = permissionTable.getSelectedRows();
			final List<Permission> selectedPermissions = new ArrayList<Permission>();
			for (final int index : selectedRows) {
				selectedPermissions.add(currentFEntry.get().getPermissions().get(index));
			}

			if (currentFEntry.get().getPermissionOfCurrentUser().getManageAllowed()) {
				for (final Permission permission : selectedPermissions) {
					if (showOwnPermissionWarning(permission)) {
						currentFEntry.get().setPermission(permission.getUser(), false, false, false);
					}
				}
			} else {
				optionPane.showMessageDialog("Sie besitzen leider nicht die erforderlichen Rechte für diese Änderung.");
			}
		}
	};

	private Boolean showOwnPermissionWarning(final Permission permission) {
		Boolean agreed = true;
		if (permission.getUser().getEmail().equals(userAPI.getCurrentUser().getEmail())) {
			agreed = optionPane.showConfirmationDialog("Achtung Sie ändern Ihre eigenen Rechte. Diese Aktion wirklich durchführen?") < 1;
		}
		return agreed;
	}
}
