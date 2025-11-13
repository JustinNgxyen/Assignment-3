package model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SearchTracksTest {
    @Test
    void testSearchTracksReturnsResults() throws Exception {
        APIClient client = new APIClient();
        client.authenticate();
        List<Track> results = client.searchTracks("Muse", 5);
        assertFalse(results.isEmpty());
    }

}
