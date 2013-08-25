package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.enums.StorageLimit;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountingControllerTest {

	@Mock
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private AccountingController accountingController;

	@Before
	public void setUp() {
		final User testUser = new User();
		when(mockedAPI.getCurrentUser()).thenReturn(testUser);

		accountingController.show();
	}

	/**
	 * Testet das Ändern von Daten. Simuliert dabei einen eingeloggten Nutzer, der seine Daten ändern möchte und
	 * ändert diese dann.
	 */
	@Test
	public void testSuccessfulChange() {
		when(mockedAPI.changeAccountingSettings(Matchers.any(User.class))).thenReturn(true);

		accountingController.streetField.setText("TestStr");
		accountingController.codeField.setText("12345");
		accountingController.countryField.setText("Land");
		accountingController.locationField.setText("Stadt");
		accountingController.storageLimitField.setSelectedItem(StorageLimit.GB_20);

		accountingController.save();

		final ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
		verify(mockedAPI).changeAccountingSettings(user.capture());

		assertThat(user.getValue().getAddressInfo().getStreet()).isEqualTo("TestStr");
		assertThat(user.getValue().getAddressInfo().getCity()).isEqualTo("Stadt");
		assertThat(user.getValue().getAddressInfo().getCountry()).isEqualTo("Land");
		assertThat(user.getValue().getAddressInfo().getZipCode()).isEqualTo("12345");
		assertThat(user.getValue().getStorageLimit()).isEqualTo(StorageLimit.GB_20);

		verify(optionPaneHelper).showMessageDialog(contains("Zur Erhöhung der Speicherkapazität müssen Sie einen Zahlungsvorgang durchführen."));
		verify(optionPaneHelper).showMessageDialog("Die Änderung war erfolgreich");
	}

	@Test
	public void testValidationOfAddressDataWhenIncreasingStorageLimit() {
		accountingController.locationField.setText("Stadt");
		accountingController.storageLimitField.setSelectedItem(StorageLimit.GB_20);

		accountingController.save();

		verify(mockedAPI, never()).changeAccountingSettings(any(User.class));
		verify(optionPaneHelper).showMessageDialog("Sie müssen erst die Zahlungsinformationen angeben, bevor sie ihre Speicherkapazität erhöhen können!");
	}

	@Test
	public void testReactionOnAPIReportsErrorOnChange() {
		when(mockedAPI.changeAccountingSettings(Matchers.any(User.class))).thenReturn(false);
		accountingController.save();

		verify(mockedAPI).changeAccountingSettings(Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("Das Ändern der Daten ist fehlgeschlagen!");
	}

	/**
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	@Test
	public void testStop() {
		accountingController.stop();
		verify(optionPaneHelper).showMessageDialog(anyString());
	}
}

