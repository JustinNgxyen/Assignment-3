package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SearchArtistByNameTest {
    @Test
    void testSearchArtistByNameReturnsArtist() throws Exception {
        APIClient client = new APIClient();
        client.authenticate();
        List<Artist> artists = client.searchArtistByName("Coldplay");
        assertFalse(artists.isEmpty());
    }

}
