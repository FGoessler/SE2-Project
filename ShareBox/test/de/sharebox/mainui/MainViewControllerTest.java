package de.sharebox.mainui;

import de.sharebox.api.UserAPI;
import de.sharebox.file.controller.DirectoryViewControllerFactory;
import de.sharebox.file.controller.LogViewControllerFactory;
import de.sharebox.file.controller.PermissionViewControllerFactory;
import de.sharebox.mainui.menu.AdministrationMenuFactory;
import de.sharebox.mainui.menu.FileMenuFactory;
import de.sharebox.user.controller.*;
import de.sharebox.user.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainViewControllerTest {

	@Mock
	private User currentUser;
	@Mock
	private UserAPI mockedUserAPI;
	@Mock
	private LoginController loginController;
	@Mock
	private PermissionViewControllerFactory permissionViewControllerFactory;
	@Mock
	private LogViewControllerFactory logViewControllerFactory;
	@Mock
	private DirectoryViewControllerFactory directoryViewControllerFactory;
	@Mock
	private FileMenuFactory fileMenuFactory;
	@Mock
	private AdministrationMenuFactory administrationMenuFactory;
	@Mock
	private AccountingController accountingController;
	@Mock
	private ChangeCredentialsController changeCredentialsController;
	@Mock
	private EditProfileController editProfileController;
	@Mock
	private InvitationController invitationController;

	@InjectMocks
	private MainViewController mainView;

	@Test
	public void providesAUserObject() {
		assertThat(mainView.getCurrentUser()).isSameAs(currentUser);
	}

	@Test
	public void containsSeveralController() {
		verify(directoryViewControllerFactory).create(any(JTree.class));
		verify(permissionViewControllerFactory).create(any(JSplitPane.class));
		verify(logViewControllerFactory).create(any(JSplitPane.class));
		verify(fileMenuFactory).create(any(JMenuBar.class));
		verify(administrationMenuFactory).create(any(JMenuBar.class), same(mainView));
	}

	@Test
	public void canOpenAAccountingController() {
		mainView.openAccountController();

		verify(accountingController).show();
	}

	@Test
	public void canOpenAChangeCredentialsController() {
		mainView.openEditCredentialsController();

		verify(changeCredentialsController).show();
	}

	@Test
	public void canOpenAEditProfileController() {
		mainView.openEditProfileController();

		verify(editProfileController).show();
	}

	@Test
	public void canOpenAInvitationController() {
		mainView.openInvitationController();

		verify(invitationController).show();
	}

	@Test
	public void canPerformALogoutProcess() {
		mainView.close();

		verify(mockedUserAPI).logout();
		verify(loginController).show();
	}
}
