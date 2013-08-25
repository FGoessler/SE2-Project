package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.enums.StorageLimit;
import de.sharebox.user.model.AddressInfo;
import de.sharebox.user.model.User;

import javax.swing.*;

import static com.google.common.base.Strings.isNullOrEmpty;

/** 
 * TODO Klassenbeschreibung (Eingabefelder für Accounting?)
 */
public class AccountingController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private int oldStorageLimitIndex;

	private JFrame frame;
	protected JComboBox<StorageLimit> storageLimitField;
	protected JTextField streetField;
	protected JTextField additiveField;
	protected JTextField codeField;
	protected JTextField locationField;
	protected JTextField countryField;

	/**
	 * Erstellt einen neuen AccountingController.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
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
	 * Öffnen des Fensters für die Accountbearbeitung. Hierbei werden die Textfelder, nach Überprüfung, mit den
	 * bereits bekannten Informationen ausgefüllt.
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
	 * TODO der Text ist etwas verwirrend, weiß leider nicht genau was gemeint ist um es selber zu ändern
	 * 
	 * Speichern der Änderungen der Account-Informationen. Für jedes Feld wird überprüft, ob eine Eingabe optional<br/>
	 * ist oder nicht (z.B.: >5GB Speicherplatz). Wenn eine Eingabe optional ist, kann trotzdem eine Information<br/>
	 * eingetragen werden (wird gespeichert). Beim Speichern des Speicherplatzes wird überprüft, ob alle nötigen<br/>
	 * Zahlungsinformationen eingetragen sind, anschließend wird man ggf. an das externe Bezahlsystem weitergeleitet.
	 */
	public void save() {
		final User user = new User();
		final AddressInfo addressInfo = user.getAddressInfo();

		addressInfo.setStreet(streetField.getText());
		addressInfo.setZipCode(codeField.getText());
		addressInfo.setCity(locationField.getText());
		addressInfo.setCountry(countryField.getText());
		addressInfo.setAdditionalStreet(additiveField.getText());

		user.setAddressInfo(addressInfo);
		user.setStorageLimit((StorageLimit) storageLimitField.getSelectedItem());

		if (!storageLimitField.getSelectedItem().equals(StorageLimit.GB_5) &&
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


	/**
	 * Ein einfacher Button zum Abbrechen, der das Fenster ohne Änderungen schließt.<br/>
	 * Wird per SWIxml an das GUI Element gebunden.
	 */
	public void stop() {
		frame.setVisible(false);
		optionPane.showMessageDialog("Sie haben den Vorgang abgebrochen!");
	}
}
