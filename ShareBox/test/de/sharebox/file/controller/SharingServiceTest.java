package de.sharebox.file.controller;

import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.FEntryPermission;
import de.sharebox.helpers.OptionPaneHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SharingServiceTest {
	private SharingService sharingService;
	private FEntry fEntry;

	@Mock
	private OptionPaneHelper mockedOptionPaneHelper;

	@Before
	public void setUp() {
		sharingService = new SharingService();
		sharingService.optionPaneHelper = mockedOptionPaneHelper;

		when(mockedOptionPaneHelper.showInputDialog(anyString(), anyString())).thenReturn("newUser@mail.com");

		fEntry = new FEntry();
	}

	@Test
	public void canShowADialogToInviteAUserAndCallsAPIMethods() {
		sharingService.showShareFEntryDialog(fEntry);

		assertThat(fEntry.getPermissions()).hasSize(1);
		FEntryPermission permission = fEntry.getPermissions().get(0);
		assertThat(permission.getUser().getEmail()).isEqualTo("newUser@mail.com");
		assertThat(permission.getReadAllowed()).isTrue();
		assertThat(permission.getWriteAllowed()).isTrue();
		assertThat(permission.getManageAllowed()).isFalse();
	}
}
