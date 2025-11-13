package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InvalidDataHandlingTest {
    @Test
    void testInvalidArtistSearchDoesNotCrash() throws Exception {
        APIClient client = new APIClient();
        client.authenticate();
        List<Artist> result = client.searchArtistByName("thisartistdoesnotexistatall123456");

        assertNotNull(result, "Search should return a non-null list even for nonsense input");
        System.out.println("Spotify returned " + result.size() + " items for invalid search (fuzzy match works).");
    }


}
