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
import static org.mockito.Mockito.*;

/**
 * @author Benjamin Barth
 * @author Kay Thorsten Meißner
 */

@RunWith(MockitoJUnitRunner.class)
public class InvitationControllerTest {

	@Mock
	private UserAPI mockedAPI;
	private InvitationController invitationController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		Main.mainWindowViewController = null;

		invitationController = new InvitationController();
	}

	@After
	public void tearDown() {
		Main.mainWindowViewController = null;
		UserAPI.resetSingletonInstance();
	}

	/* 
	 * Testet das erfolgreiche Einladen einer fremden Person
	 */
	
	@Test
	public void testInviteTrue() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(true);
		invitationController.mailField.setText("kurt");
		invitationController.invite.actionPerformed(mock(ActionEvent.class));
		verify(mockedAPI).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
	}
	
	/*
	 * Testet, den Fall, dass der Nutzer eine bereits bekannte oder falsche E-Mail Adresse angibt.
	 */
	
	@Test
	public void testChangeFalse() {
		when(mockedAPI.inviteUser(Matchers.any(User.class), Matchers.any(User.class))).thenReturn(false);
		invitationController.invite.actionPerformed(mock(ActionEvent.class));
		verify(mockedAPI).inviteUser(Matchers.any(User.class), Matchers.any(User.class));
	}

	/*
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	
	@Test
	public void testStop() {
		invitationController.stop.actionPerformed(mock(ActionEvent.class));
		
	}
	
}

