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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvitationControllerTest {

	@Mock
	private UserAPI mockedAPI;
	@Mock
	private OptionPaneHelper optionPaneHelper;

	@InjectMocks
	private InvitationController invitationController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		invitationController.show();
	}

	@After
	public void tearDown() {
		UserAPI.resetSingletonInstance();
	}

	/**
	 * Testet das erfolgreiche Einladen einer fremden Person.
	 */
	@Test
	public void testSuccessfulInvite() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(true);
		invitationController.mailField.setText("kurt@haha.de");
		invitationController.invite.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("kurt@haha.de wurde eingeladen!");
	}

	/**
	 * Testet, den Fall, dass der Nutzer eine bereits bekannte oder falsche E-Mail Adresse angibt.
	 */
	@Test
	public void testInvalidInvite() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(false);
		invitationController.mailField.setText("kurt@haha.de");
		invitationController.invite.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
		verify(optionPaneHelper).showMessageDialog("kurt@haha.de ist bereits registriert oder die Emailadresse ist ungültig!");
	}

	/**
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	@Test
	public void testStop() {
		invitationController.stop.actionPerformed(mock(ActionEvent.class));
		verify(optionPaneHelper).showMessageDialog(anyString());
	}

}

