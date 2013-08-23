package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.user.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

	@Mock
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private RegisterController registerController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		registerController.show();
	}

	@After
	public void tearDown() {
		UserAPI.resetSingletonInstance();
	}

	/**
	 * Testet das erfolgreiche Registrieren eines Nutzers.
	 */
	@Test
	public void testSuccessfulRegister() {
		when(mockedAPI.registerUser(Matchers.any(User.class))).thenReturn(true);

		registerController.mailField.setText("Nutzername");
		registerController.passwordField1.setText("Passwort123");
		registerController.passwordField2.setText("Passwort123");
		registerController.firstnameField.setText("kurt");
		registerController.lastnameField.setText("kanns");
		registerController.genderField.setText("m");
		registerController.streetField.setText("Street");
		registerController.codeField.setText("12345");
		registerController.countryField.setText("Base");
		registerController.locationField.setText("preBase");

		registerController.register.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).registerUser(any(User.class));
		verify(optionPaneHelper).showMessageDialog("Die Registrierung war erfolgreich");
	}

	/**
	 * Testet, den Fall, dass der Nutzer beim Speichern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	@Test
	public void testInvalidRegister() {
		when(mockedAPI.registerUser(Matchers.any(User.class))).thenReturn(false);

		registerController.register.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).registerUser(any(User.class));
		verify(optionPaneHelper).showMessageDialog("Die Registrierung ist fehlgeschlagen!");
	}

	/**
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	@Test
	public void testStop() {
		registerController.stop.actionPerformed(mock(ActionEvent.class));
		verify(optionPaneHelper).showMessageDialog(anyString());
	}

}

