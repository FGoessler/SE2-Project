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
public class ChangeCredentialsControllerTest{

	@Mock
	private UserAPI mockedAPI;
	private ChangeCredentialsController changeCredentialsController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		
		changeCredentialsController = new ChangeCredentialsController();
	}

	@After
	public void tearDown() {
		Main.mainWindowViewController = null;
		UserAPI.resetSingletonInstance();
	}

	/*
	 * Testet das ändern von Daten. Simuliert dabei einen eingeloggten Nutzer, der seine Daten ändern möchte und 
	 * ändert diese dann.
	 */
	
	@Test
	public void testChangeTrue() {
		when(mockedAPI.changeCredential(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(true);
		
		changeCredentialsController.eMailField.setText("kurt");
		changeCredentialsController.oldPasswordField.setText("old");
		changeCredentialsController.newPasswordField.setText("new");
		changeCredentialsController.newPasswordField1.setText("new");

		changeCredentialsController.save.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).changeCredential(Matchers.any(User.class), Matchers.any(User.class));
		
		assertThat(Main.mainWindowViewController.getCurrentUser().getEmail()).isEqualTo("kurt");
		assertThat(Main.mainWindowViewController.getCurrentUser().getPassword()).isEqualTo("new");
	}
	
	/*
	 * Testet, den Fall, dass der Nutzer beim ändern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	
	@Test
	public void testChangeFalse() {
		when(mockedAPI.changeCredential(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(false);
		changeCredentialsController.save.actionPerformed(mock(ActionEvent.class));
		verify(mockedAPI).changeCredential(Matchers.any(User.class), Matchers.any(User.class));
	}

	/*
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	
	@Test
	public void testStop() {
		changeCredentialsController.stop.actionPerformed(mock(ActionEvent.class));	
	}
}

