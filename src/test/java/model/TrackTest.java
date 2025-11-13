package model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TrackTest {
    @Test
    void testTrackProperties() {
        Track t = new Track("1", "Song", List.of("Artist"), "Album");
        assertEquals("Song", t.getName());
    }
}
