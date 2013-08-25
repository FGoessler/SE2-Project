package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.enums.StorageLimit;
import de.sharebox.user.model.AddressInfo;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.google.common.base.Strings.isNullOrEmpty;


public class AccountingController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	public JComboBox<StorageLimit> storageLimitField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;
	public int oldStorageLimitIndex;

	/**
	 * Erstellt einen neuen AccountingController. <br/>
	 * Instanzen dieser Klasse solten per Dependency Injection durch Guice erstellt werden.
	 *
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	AccountingController(final OptionPaneHelper optionPaneHelper,
						 final UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Accounting-bearbeiten Fensters. Hierbei werden alle bereits bekannten Informationen in
	 * die TextFields direkt mit übernommen. Außerdem wird überprüft,ob bereits Informationen zu den einzelnen Feldern
	 * vorhanden waren oder nicht.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/editAccounting");
		frame.setVisible(true);

		final User user = userAPI.getCurrentUser();

		oldStorageLimitIndex = 0;
		for (int i = 0; i < storageLimitField.getItemCount(); i++) {
			if (user.getStorageLimit() != null && user.getStorageLimit().equals(storageLimitField.getItemAt(i))) {
				oldStorageLimitIndex = i;
			}
		}

		final AddressInfo addressInfo = user.getAddressInfo();
		storageLimitField.setSelectedIndex(oldStorageLimitIndex);
		streetField.setText(addressInfo.getStreet());
		additiveField.setText(addressInfo.getAdditionalStreet());
		codeField.setText(addressInfo.getZipCode());
		locationField.setText(addressInfo.getCity());
		countryField.setText(addressInfo.getCountry());
	}

	/**
	 * Speichern der Änderungen an den Accounting Informationen. Für jedes einzelne Feld wird überprüft, ob etwas hinein
	 * geschrieben werden muss oder nicht. Wenn nichts drin stehen muss kann auch beim Speichern nichts drin stehen
	 * Wenn zuvor was drin Stand, dann muss auch wieder eine Information eingetragen werden. Beim speichern des
	 * Speicherplatzes wird überprüft, ob alle Zahlungsinformationen eingetragen sind, anschließend wird man an
	 * das externe Bezahlsystem weitergeleitet.
	 */
	public Action save = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			final User user = new User();
			final AddressInfo addressInfo = user.getAddressInfo();

			addressInfo.setStreet(streetField.getText());
			addressInfo.setZipCode(codeField.getText());
			addressInfo.setCity(locationField.getText());
			addressInfo.setCountry(countryField.getText());
			addressInfo.setAdditionalStreet(additiveField.getText());

			user.setAddressInfo(addressInfo);
			user.setStorageLimit((StorageLimit) storageLimitField.getSelectedItem());

			if (storageLimitField.getSelectedIndex() > 0 &&
					(isNullOrEmpty(addressInfo.getStreet()) || isNullOrEmpty(addressInfo.getCity()) ||
							isNullOrEmpty(addressInfo.getZipCode()) || isNullOrEmpty(addressInfo.getCountry()))) {
				optionPane.showMessageDialog("Sie müssen erst die Zahlungsinformationen angeben, bevor sie ihre " +
						"Speicherkapazität erhöhen können!");
			} else {
				if (oldStorageLimitIndex < storageLimitField.getSelectedIndex()) {
					optionPane.showMessageDialog("Zur Erhöhung der Speicherkapazität müssen Sie einen Zahlungsvorgang " +
							"durchführen. Dies ist in diesem Prototyp nicht umgesetzt. Eine entsprechende Integration eines " +
							"Systems eines Drittanbieters käme an dieser Stelle.");
				}

				if (userAPI.changeAccountingSettings(user)) {
					frame.setVisible(false);
					optionPane.showMessageDialog("Die Änderung war erfolgreich");
				} else {
					optionPane.showMessageDialog("Das Ändern der Daten ist fehlgeschlagen!");
				}
			}
		}
	};

	/**
	 * Ein einfacher Abbrechen Button, der das Fenster schließt und nichts ändert.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
		}
	};

	/**
	 * Prüfen was in der ComboBox ausgewählt wurde
	 */
	public Action selectBoxAction = new AbstractAction() {
		public void actionPerformed(final ActionEvent event) {
			System.out.println(((JComboBox) event.getSource()).getSelectedItem().toString());
		}
	};
}
