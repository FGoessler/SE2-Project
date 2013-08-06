package de.sharebox.user.controller;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.user.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
	}

	@Test
	public void testLogin() {
		when(mockedAPI.login(Matchers.any(User.class))).thenReturn(true);

		loginController.mailField.setText("Nutzername");
		loginController.passwordField.setText("Passwort123");

		loginController.submit.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).login(any(User.class));
		assertThat(Main.mainWindowViewController.getCurrentUser().getEmail()).isEqualTo("Nutzername");
	}

	@Ignore
	@Test
	public void testRegister() {
		//TODO: write test and implementation for register!
	}
}
