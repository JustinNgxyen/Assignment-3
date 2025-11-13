
package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {
    @Test
    void testArtistModel() {
        Artist a = new Artist("123", "Queen");
        assertEquals("123", a.getId());
        assertEquals("Queen", a.getName());
    }
}
