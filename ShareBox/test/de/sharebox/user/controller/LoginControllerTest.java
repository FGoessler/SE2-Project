package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
import de.sharebox.mainui.MainViewControllerFactory;
import de.sharebox.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
	@Mock
	private MainViewControllerFactory mainViewControllerFactory;
	@Mock
	private RegisterController registerController;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private LoginController loginController;

	@Before
	public void setUp() {
		loginController.show();
	}

	@Test
	public void testSuccessfulLogin() {
		when(mockedAPI.login(Matchers.any(User.class))).thenReturn(true);

		loginController.mailField.setText("Nutzername");
		loginController.passwordField.setText("Passwort123");

		loginController.submit.actionPerformed(mock(ActionEvent.class));

		final ArgumentCaptor<User> user = ArgumentCaptor.forClass(User.class);
		verify(mockedAPI).login(user.capture());
		assertThat(user.getValue().getEmail()).isEqualTo("Nutzername");
		assertThat(user.getValue().getPassword()).isEqualTo("Passwort123");
		verify(mainViewControllerFactory).create(same(user.getValue()), same(loginController));
	}


	@Test
	public void testInvalidLogin() {
		when(mockedAPI.login(Matchers.any(User.class))).thenReturn(false);

		loginController.submit.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).login(any(User.class));
		verify(mainViewControllerFactory, never()).create(any(User.class), same(loginController));
		verify(optionPaneHelper).showMessageDialog("Login-Informationen falsch! Bitte geben sie ihre Daten erneut ein.");
	}


	@Test
	public void canShowARegisterController() {
		loginController.register.actionPerformed(mock(ActionEvent.class));
		verify(registerController).show();
	}
}
