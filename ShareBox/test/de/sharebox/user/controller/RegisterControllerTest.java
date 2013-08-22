package de.sharebox.user.controller;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

	@Mock
	private UserAPI mockedAPI;
	private RegisterController registerController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		Main.mainWindowViewController = null;

		registerController = new RegisterController();
	}

	@After
	public void tearDown() {
		Main.mainWindowViewController = null;
		UserAPI.resetSingletonInstance();
	}

	/*
	 * Testet das erfolgreiche registrieren eines Nutzers
	 */
	
	@Test
	public void testRegisterTrue() {
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
		registerController.storageLimitField.setSelectedIndex(2);

		registerController.register.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).registerUser(any(User.class));
	}
	
	/*
	 * Testet, den Fall, dass der Nutzer beim speichern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	
	@Test
	public void testRegisterFalse() {
		when(mockedAPI.registerUser(Matchers.any(User.class))).thenReturn(false);
			
		registerController.register.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).registerUser(any(User.class));
	}

	/*
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	
	@Test
	public void testStop() {
		registerController.stop.actionPerformed(mock(ActionEvent.class));
		
	}
	
}

