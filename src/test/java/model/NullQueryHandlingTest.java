package model;

import controller.MainController;
import org.junit.jupiter.api.Test;
import view.MainFrame;

import static org.mockito.Mockito.*;

public class NullQueryHandlingTest {
    @Test
    void testPerformSearchWithEmptyQueryShowsError() {
        MainFrame mockView = mock(MainFrame.class);
        MainController controller = new MainController(mockView);
        controller.performSearch("");
        verify(mockView).showError("Please enter a search query");
    }
}
