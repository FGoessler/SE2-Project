package de.sharebox.file.controller;

import de.sharebox.file.model.Directory;
import de.sharebox.file.model.FEntry;
import de.sharebox.file.model.File;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class DirectoryViewClipboardServiceTest {

	private DirectoryViewClipboardService clipboard;
	private File testFile;
	private Directory testDirectory;

	@Before
	public void setUp() {
		clipboard = new DirectoryViewClipboardService();

		testFile = new File();
		testFile.setName("newFile");

		testDirectory = new Directory();
		testDirectory.setName("lol");
		testDirectory.createNewFile("subFile1");
		testDirectory.createNewFile("subFile2");
	}

	@Test
	public void canAddFEntriesToClipboardAndPasteThem() {
		clipboard.addToClipboard(testDirectory);
		clipboard.addToClipboard(testFile);

		Directory targetDirectory = new Directory();
		File keptFile = targetDirectory.createNewFile("file to be kept");
		clipboard.pasteClipboardContent(targetDirectory);

		//check that no file in the target directory got deleted
		assertThat(targetDirectory.getFEntries().get(0)).isEqualTo(keptFile);
		assertThat(targetDirectory.getFEntries().get(0)).isSameAs(keptFile);

		//check that copied objects are deep copies
		assertThat(targetDirectory.getFEntries().get(2).getName()).isEqualTo(testFile.getName());
		assertThat(targetDirectory.getFEntries().get(2)).isNotSameAs(testFile);
		assertThat(targetDirectory.getFEntries().get(1).getName()).isEqualTo(testDirectory.getName());
		FEntry child = ((Directory)targetDirectory.getFEntries().get(1)).getFEntries().get(0);
		assertThat(child.getName()).isEqualTo(testDirectory.getFEntries().get(0).getName());
		assertThat(targetDirectory.getFEntries().get(1)).isNotSameAs(testDirectory);
	}

	@Test
	public void clipboardOnlyResetsByCallingResetClipboard() {
		clipboard.addToClipboard(testDirectory);

		clipboard.resetClipboard();

		clipboard.addToClipboard(testFile);

		Directory targetDirectory = new Directory();
		clipboard.pasteClipboardContent(targetDirectory);

		assertThat(targetDirectory.getFEntries()).hasSize(1);
		assertThat(targetDirectory.getFEntries().get(0).getName()).isEqualTo(testFile.getName());


	}

	@Test
	public void canAddAListOfFEntries() {
		ArrayList<FEntry> fEntries = new ArrayList<FEntry>();
		fEntries.add(testDirectory);
		fEntries.add(testFile);
		clipboard.addToClipboard(fEntries);

		Directory targetDirectory = new Directory();
		clipboard.pasteClipboardContent(targetDirectory);

		assertThat(targetDirectory.getFEntries().get(1).getName()).isEqualTo(testFile.getName());
		assertThat(targetDirectory.getFEntries().get(0).getName()).isEqualTo(testDirectory.getName());
	}
}
