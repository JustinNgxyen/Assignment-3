package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class GetTopTracksByArtistTest {
    @Test
    void testGetTopTracksForArtist() throws Exception {
        APIClient client = new APIClient();
        client.authenticate();
        List<Artist> artists = client.searchArtistByName("Adele");
        assertFalse(artists.isEmpty());
        String artistId = artists.get(0).getId();
        List<Track> tracks = client.getTopTracksForArtist(artistId, "US");
        assertFalse(tracks.isEmpty());
    }

}
