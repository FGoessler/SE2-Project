package de.sharebox.file.model;

import de.sharebox.user.model.User;

public class FEntryPermission {
	private Boolean readAllowed = false;
	private Boolean writeAllowed = false;
	private Boolean manageAllowed = false;

	private final User user;
	private final FEntry fEntry;

	public FEntryPermission(User user, FEntry fEntry) {
		this.user = user;
		this.fEntry = fEntry;
	}

	public User getUser() {
		return user;
	}

	public FEntry getFEntry() {
		return fEntry;
	}

	public Boolean getReadAllowed() {
		return readAllowed;
	}

	public void setReadAllowed(Boolean readAllowed) {
		this.readAllowed = readAllowed;
	}

	public Boolean getWriteAllowed() {
		return writeAllowed;
	}

	public void setWriteAllowed(Boolean writeAllowed) {
		this.writeAllowed = writeAllowed;
	}

	public Boolean getManageAllowed() {
		return manageAllowed;
	}

	public void setManageAllowed(Boolean manageAllowed) {
		this.manageAllowed = manageAllowed;
	}
}
