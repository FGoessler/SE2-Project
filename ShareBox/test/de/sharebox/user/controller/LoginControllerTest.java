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
public class LoginControllerTest {

	@Mock
	private UserAPI mockedAPI;
	private LoginController loginController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		Main.mainWindowViewController = null;

		loginController = new LoginController();
	}

	@After
	public void tearDown() {
		Main.mainWindowViewController = null;
		UserAPI.resetSingletonInstance();
	}

	/*
	 * Es wird fiktiv ein Login durchgeführt, um zu testen, ob das MainFenster nach einem Login geöffnet wird.
	 */
	
	@Test
	public void testLoginTrue() {
		when(mockedAPI.login(Matchers.any(User.class))).thenReturn(true);

		loginController.mailField.setText("Nutzername");
		loginController.passwordField.setText("Passwort123");

		loginController.submit.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).login(any(User.class));
		assertThat(Main.mainWindowViewController.getCurrentUser().getEmail()).isEqualTo("Nutzername");
		assertThat(Main.mainWindowViewController.getCurrentUser().getPassword()).isEqualTo("Passwort123");
	}
	
	/*
	 * Es wird getestet, was passiert wenn der Login fehlschlägt und ob dann die Nachricht für den Nutzer angezeigt wird.
	 */
	
	@Test
	public void testLoginFalse() {
		when(mockedAPI.login(Matchers.any(User.class))).thenReturn(false);
			
		loginController.submit.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).login(any(User.class));
	}

	/*
	 * Der Registrieren-Button wird überprüft.
	 */
	
	@Test
	public void testRegister() {
		loginController.register.actionPerformed(mock(ActionEvent.class));
		
	}
}
