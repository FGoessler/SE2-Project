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
public class AccuntingControllerTest{

	@Mock
	private UserAPI mockedAPI;
	private AccountingController accountingController;


	@Before
	public void setUp() {
		UserAPI.injectSingletonInstance(mockedAPI);
		accountingController = new AccountingController();
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
		when(mockedAPI.changeAccountingSettings(Matchers.any(User.class))).thenReturn(true);
		
		accountingController.streetField.setText("TestStr");
		accountingController.codeField.setText("12345");
		accountingController.countryField.setText("Land");
		accountingController.locationField.setText("Stadt");
		accountingController.storageLimitField.setSelectedIndex(2);

		accountingController.save.actionPerformed(mock(ActionEvent.class));

		verify(mockedAPI).changeAccountingSettings(Matchers.any(User.class));
		
		assertThat(Main.mainWindowViewController.getCurrentUser().getPaymentInfo().getStreet()).isEqualTo("TestStr");
		assertThat(Main.mainWindowViewController.getCurrentUser().getPaymentInfo().getCity()).isEqualTo("Stadt");
		assertThat(Main.mainWindowViewController.getCurrentUser().getPaymentInfo().getCountry()).isEqualTo("Land");
		assertThat(Main.mainWindowViewController.getCurrentUser().getPaymentInfo().getZipCode()).isEqualTo("12345");
		assertThat(Main.mainWindowViewController.getCurrentUser().getStorageLimit()).isEqualTo("20GB");
	}
	
	/*
	 * Testet, den Fall, dass der Nutzer beim ändern seiner Daten irgendwas falsch oder gar nicht angibt.
	 */
	
	@Test
	public void testChangeFalse() {
		when(mockedAPI.changeAccountingSettings(Matchers.any(User.class))).thenReturn(false);
		accountingController.save.actionPerformed(mock(ActionEvent.class));
		verify(mockedAPI).changeAccountingSettings(Matchers.any(User.class));
	}

	/*
	 * Testet den Abbrechen-Button und die Aktion die dabei ausgeführt werden soll.
	 */
	
	@Test
	public void testStop() {
		accountingController.stop.actionPerformed(mock(ActionEvent.class));	
	}
}

