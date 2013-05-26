package de.sharebox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExampleWindowTest {
    private ExampleWindow window;

    @Mock
    private ExampleWindow anotherWindow;

    @Before
    public void setUp() throws Exception {
        window = new ExampleWindow();
    }

    @Test
    public void aSmallTest() throws Exception {
        //J-Unit native
        assertEquals("Textfield should have some value!", "Swixml", window.textField.getText());

        //FEST asserts
        assertThat(window.textField.getText()).isEqualTo("Swixml");
    }

    @Test
    public void aMockitoTest() throws Exception {
        when(anotherWindow.getCounterText()).thenReturn("Wadde hadde du de da?!");

        assertThat(anotherWindow.getCounterText()).isEqualTo("Wadde hadde du de da?!");

        verify(anotherWindow).getCounterText();
    }
}
