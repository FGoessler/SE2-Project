package de.sharebox.mainui;

import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewControllerFactory;
import de.sharebox.file.controller.PermissionViewControllerFactory;
import de.sharebox.mainui.menu.FileMenuFactory;
import de.sharebox.user.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainViewControllerTest {
	@Mock
	private ActionEvent mockedActionEvent;
	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private User currentUser;

	@Mock
	private PermissionViewControllerFactory permissionViewControllerFactory;
	@Mock
	private DirectoryViewControllerFactory directoryViewControllerFactory;
	@Mock
	private FileMenuFactory fileMenuFactory;

	@InjectMocks
	private MainViewController mainView;

	@Test
	public void providesAUserObject() {
		assertThat(mainView.getCurrentUser()).isSameAs(currentUser);
	}

	@Test
	public void containsADirectoryViewController() {
		verify(directoryViewControllerFactory).create(any(JTree.class));
	}

	@Test
	public void clickingEditAccountingShowsAccountingController() {
		mainView.openAccountController();

		assertThat(mainView.accountController).isNotNull();
	}

	@Test
	public void clickingEditProfileShowsEditProfileController() {
		mainView.openEditProfileController();

		assertThat(mainView.editProfileController).isNotNull();
	}

	@Test
	public void clickingInviteUserShowsInvitationController() {
		mainView.openInvitationController();

		assertThat(mainView.invitationController).isNotNull();
	}

	@Test
	public void clickingLogoutPerformsLogoutProcess() {
		UserAPI.injectSingletonInstance(mockedUserAPI);

		mainView.close();

		verify(mockedUserAPI).logout();
	}
}
