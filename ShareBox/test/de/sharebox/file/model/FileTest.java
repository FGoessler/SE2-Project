package de.sharebox.file.model;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class FileTest {
    private File file;
    
    @Before
    public void setUp() throws Exception {
        file = new File();
    }

    @Test
    public void isSubclassOfFEntry() {
        assertThat(file).isInstanceOf(FEntry.class);
    }

    @Test
    public void providesAccessToAFileName() {
        file.setFileName("testFile");

        assertThat(file.getFileName()).isEqualTo("testFile");

        //TODO: notifications!
    }


}
