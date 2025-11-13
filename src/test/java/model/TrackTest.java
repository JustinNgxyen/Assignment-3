
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrackTest {          // ✅ class declaration required
    @Test
    void testTrackProperties() {
        Track t = new Track("1", "Song", List.of("Artist"), "Album");
        assertEquals("Song", t.getName());
    }
}
