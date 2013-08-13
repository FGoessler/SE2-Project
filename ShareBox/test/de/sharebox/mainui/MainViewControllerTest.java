package de.sharebox.mainui;

import de.sharebox.Main;
import de.sharebox.api.UserAPI;
import de.sharebox.user.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainViewControllerTest {
	private MainViewController mainView;

	@Mock
	private ActionEvent mockedActionEvent;
	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private User currentUser;

	@Before
	public void setUp() {
		Main.loginController = null;
		mainView = new MainViewController(currentUser);
	}

	@After
	public void tearDown() {
		Main.loginController = null;
	}

	@Test
	public void providesAUserObject() {
		assertThat(mainView.getCurrentUser()).isSameAs(currentUser);
	}

	@Test
	public void containsADirectoryViewControllerAndIsVisible() {
		assertThat(mainView.directoryViewController).isNotNull();

		assertThat(mainView.swix.getRootComponent().isVisible()).isTrue();
	}

	@Test
	public void canOpenAAccountingController() {
		mainView.openAccountController();

		assertThat(mainView.accountController).isNotNull();
	}

	@Test
	public void canOpenAChangeCredentialsController() {
		mainView.openEditCredentialsController();

		assertThat(mainView.editCredentialsController).isNotNull();
	}

	@Test
	public void canOpenAEditProfileController() {
		mainView.openEditProfileController();

		assertThat(mainView.editProfileController).isNotNull();
	}

	@Test
	public void canOpenAInvitationController() {
		mainView.openInvitationController();

		assertThat(mainView.invitationController).isNotNull();
	}

	@Test
	public void canPerformALogoutProcess() {
		UserAPI.injectSingletonInstance(mockedUserAPI);

		mainView.close();

		verify(mockedUserAPI).logout();
		assertThat(Main.loginController).isNotNull();
	}
}
