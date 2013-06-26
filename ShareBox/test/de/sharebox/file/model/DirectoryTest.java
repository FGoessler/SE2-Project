package de.sharebox.file.model;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class DirectoryTest {

    private Directory directory;

    private static final String TEST_FILENAME = "TestFile";
    private static final String TEST_DIRNAME = "TestDirectory";

    @Before
    public void setUp() {
        directory = new Directory();
    }

    @Test
    public void isFEntrySubclass() {
        assertThat(directory).isInstanceOf(FEntry.class);
    }

    @Test
    public void hasAName() {
        directory.setName(TEST_DIRNAME);
        assertThat(directory.getName()).isEqualTo(TEST_DIRNAME);
        //TODO: notifications!
    }

    @Test
    public void canCreateNewSubFiles() {
        File createdFile = directory.createNewFile(TEST_FILENAME);

        assertThat(createdFile.getFileName()).isEqualTo(TEST_FILENAME);
        assertThat(directory.getFEntries()).contains(createdFile);
    }

    @Test
    public void canCreateNewSubDirectories() {
        Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);

        assertThat(createdDirectory.getName()).isEqualTo(TEST_DIRNAME);
        assertThat(directory.getFEntries()).contains(createdDirectory);
    }

    @Test
    public void canContainMultipleFEntries() {
        File createdFile = directory.createNewFile(TEST_FILENAME);
        Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);

        assertThat(directory.getFEntries()).hasSize(2);
        assertThat(directory.getFEntries()).contains(createdFile);
        assertThat(directory.getFEntries()).contains(createdDirectory);
    }

    @Test
    public void canRemoveFiles() {
        File createdFile = directory.createNewFile(TEST_FILENAME);
        assertThat(directory.getFEntries()).contains(createdFile);

        directory.deleteFEntry(createdFile);
        assertThat(createdFile).isNotIn(directory.getFEntries());

        //TODO: notifications!
    }

    @Test
    public void removesContentOfDirectoriesRecursively() {
        Directory createdDirectory = directory.createNewDirectory(TEST_DIRNAME);
        File createdFile = createdDirectory.createNewFile(TEST_FILENAME);

        assertThat(directory.getFEntries()).contains(createdDirectory);
        assertThat(createdDirectory.getFEntries()).contains(createdFile);

        directory.deleteFEntry(createdDirectory);
        assertThat(createdDirectory).isNotIn(directory.getFEntries());
        //TODO: check cascade!
        //TODO: notifications!
    }
}
