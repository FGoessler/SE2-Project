package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;

import java.util.ArrayList;
import java.util.List;

public class DirectoryViewClipboardService {
	private List<FEntry> clipboard = new ArrayList<FEntry>();

	public void addToClipboard(FEntry copiedFile) {
		if(copiedFile instanceof File) {
			clipboard.add(new File((File)copiedFile));
		} else {
			clipboard.add(new Directory((Directory)copiedFile));
		}
	}

	public Directory pasteClipboardContent(Directory targetDirectory) {
		if(clipboard.size() > 0) {
			targetDirectory.addFEntry(null);
		}
		return targetDirectory;
	}

	public void resetClipboard() {
		clipboard.clear();
	}

}
