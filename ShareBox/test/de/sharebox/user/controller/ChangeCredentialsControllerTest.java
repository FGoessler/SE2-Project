package de.sharebox.user.controller;

import de.sharebox.api.UserAPI;
import de.sharebox.helpers.OptionPaneHelper;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChangeCredentialsControllerTest {

	private User testUser;

	@Mock
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private ChangeCredentialsController changeCredentialsController;

	@Before
	public void setUp() {
		testUser = new User();
		testUser.setEmail("test@mail.com");
		testUser.setPassword("old");
		when(mockedAPI.getCurrentUser()).thenReturn(testUser);

		changeCredentialsController.show();
	}

	/**
	 * Testet das ändern von Daten. Simuliert dabei einen eingeloggten Nutzer, der seine Daten ändern möchte und
	 * ändert diese dann.
	 */
	@Test
	public void testSuccessfulChange() {
		when(mockedAPI.changeCredential(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(true);

		changeCredentialsController.eMailField.setText("kurt@lol.de");
		changeCredentialsController.oldPasswordField.setText("old");
		changeCredentialsController.newPasswordField.setText("new");
		changeCredentialsController.newPasswordField1.setText("new");

		changeCredentialsController.save.actionPerformed(mock(ActionEvent.class));

		final ArgumentCaptor<User> newUserData = ArgumentCaptor.forClass(User.class);
		verify(mockedAPI).changeCredential(same(testUser), newUserData.capture());

		assertThat(newUserData.getValue().getEmail()).isEqualTo("kurt@lol.de");
		assertThat(newUserData.getValue().getPassword()).isEqualTo("new");

		verify(optionPaneHelper).showMessageDialog("Ihre Änderungen wurden gespeichert!");
	}

	/**
	 * Testet, den Fall, dass der Nutzer beim ändern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	@Test
	public void testInvalidChange() {
		when(mockedAPI.changeCredential(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(false);

		changeCredentialsController.save.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).changeCredential(Matchers.any(User.class), Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("Das Ändern der Daten ist fehlgeschlagen!");
	}

	/**
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	@Test
	public void testStop() {
		changeCredentialsController.stop.actionPerformed(mock(ActionEvent.class));
		verify(optionPaneHelper).showMessageDialog(anyString());
	}
}

