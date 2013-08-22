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
public class EditProfileControllerTest {

	@Mock
	private UserAPI mockedAPI;
	private EditProfileController editProfileController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		Main.mainWindowViewController = null;

		editProfileController = new EditProfileController();
		
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
		when(mockedAPI.changeProfile(Matchers.any(User.class))).thenReturn(true);
		
		editProfileController.firstnameField.setText("hanna");
		editProfileController.lastnameField.setText("spanna");
		editProfileController.genderField.setText("w");

		editProfileController.save.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).changeProfile(any(User.class));
		
		assertThat(Main.mainWindowViewController.getCurrentUser().getFirstname()).isEqualTo("hanna");
		assertThat(Main.mainWindowViewController.getCurrentUser().getLastname()).isEqualTo("spanna");
		assertThat(Main.mainWindowViewController.getCurrentUser().getGender()).isEqualTo("w");
	}
	
	/*
	 * Testet, den Fall, dass der Nutzer beim ändern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	
	@Test
	public void testChangeFalse() {
		when(mockedAPI.changeProfile(Matchers.any(User.class))).thenReturn(false);
			
		editProfileController.save.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).changeProfile(any(User.class));
	}

	/*
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	
	@Test
	public void testStop() {
		editProfileController.stop.actionPerformed(mock(ActionEvent.class));
		
	}
	
}

