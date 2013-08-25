package de.sharebox.user.controller;

import com.google.inject.Inject;
import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.helpers.SwingEngineHelper;
import de.sharebox.user.model.PaymentInfo;
import de.sharebox.user.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.google.common.base.Strings.isNullOrEmpty;

/** 
 * TODO Klassenbeschreibung (Eingabefelder für Accounting?)
 */
public class AccountingController {
	private final OptionPaneHelper optionPane;
	private final UserAPI userAPI;

	private JFrame frame;
	public JComboBox storageLimitField;
	public JTextField streetField;
	public JTextField additiveField;
	public JTextField codeField;
	public JTextField locationField;
	public JTextField countryField;
	public int oldStorageLimitIndex;

	/**
	 * Erstellt einen neuen AccountingController.<br/>
	 * Instanzen dieser Klasse sollten per Dependency Injection durch Guice erstellt werden.
	 *
	 * TODO der optionpanehelper ist noch unklar (für mich) wie auch in den anderen Controllern
	 * @param optionPaneHelper Ein OptionPaneHelper zum Anzeigen von Dialog-Fenstern.
	 * @param userAPI          Die UserAPI zur Kommunikation mit dem Server.
	 */
	@Inject
	AccountingController(OptionPaneHelper optionPaneHelper,
						 UserAPI userAPI) {
		this.optionPane = optionPaneHelper;
		this.userAPI = userAPI;
	}

	/**
	 * Öffnen des Fensters für die Accountbearbeitung. Hierbei werden die Textfelder, nach Überprüfung, mit den <br/>
	 * bereits bekannten Informationen ausgefüllt.
	 */
	public void show() {
		frame = (JFrame) new SwingEngineHelper().render(this, "user/editAccounting");
		frame.setVisible(true);

		User user = userAPI.getCurrentUser();

		oldStorageLimitIndex = 0;
		for (int i = 0; i < storageLimitField.getItemCount(); i++) {
			if (user.getStorageLimit() != null && user.getStorageLimit().equals(storageLimitField.getItemAt(i).toString())) {
				oldStorageLimitIndex = i;
			}
		}

		PaymentInfo paymentinfo = user.getPaymentInfo();
		storageLimitField.setSelectedIndex(oldStorageLimitIndex);
		streetField.setText(paymentinfo.getStreet());
		additiveField.setText(paymentinfo.getAdditionalStreet());
		codeField.setText(paymentinfo.getZipCode());
		locationField.setText(paymentinfo.getCity());
		countryField.setText(paymentinfo.getCountry());
	}

	/**
	 * TODO der Text ist etwas verwirrend, weiß leider nicht genau was gemeint ist um es selber zu ändern
	 * 
	 * Speichern der Änderungen der Account-Informationen. Für jedes Feld wird überprüft, ob eine Eingabe optional<br/>
	 * ist oder nicht (z.B.: >5GB Speicherplatz). Wenn eine Eingabe optional ist, kann trotzdem eine Information<br/>
	 * eingetragen werden (wird gespeichert). Beim Speichern des Speicherplatzes wird überprüft, ob alle nötigen<br/>
	 * Zahlungsinformationen eingetragen sind, anschließend wird man ggf. an das externe Bezahlsystem weitergeleitet.
	 */
	public Action save = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			User user = new User();

			PaymentInfo paymentinfo = user.getPaymentInfo();

			paymentinfo.setStreet(streetField.getText());
			paymentinfo.setZipCode(codeField.getText());
			paymentinfo.setCity(locationField.getText());
			paymentinfo.setCountry(countryField.getText());
			paymentinfo.setAdditionalStreet(additiveField.getText());

			user.setPaymentInfo(paymentinfo);
			user.setStorageLimit(storageLimitField.getSelectedItem().toString());

			if (storageLimitField.getSelectedIndex() > 0 &&
					(isNullOrEmpty(paymentinfo.getStreet()) || isNullOrEmpty(paymentinfo.getCity()) ||
							isNullOrEmpty(paymentinfo.getZipCode()) || isNullOrEmpty(paymentinfo.getCountry()))) {
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
	 * Ein einfacher Button zum Abbrechen, der das Fenster ohne Änderungen schließt.
	 */
	public Action stop = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false);
			optionPane.showMessageDialog("Der Vorgang wurde abgebrochen!");
		}
	};

	/**
	 * Prüfen was in der ComboBox ausgewählt wurde
	 */
	public Action selectBoxAction = new AbstractAction() {
		public void actionPerformed(ActionEvent event) {
			System.out.println(((JComboBox) event.getSource()).getSelectedItem().toString());
		}
	};
}
