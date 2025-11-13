package model;

import org.apache.hc.core5.http.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Recommends tracks based on artist popularity.
 * Demonstrates Polymorphism - implements RecommendationStrategy.
 */
public class PopularityBasedStrategy implements RecommendationStrategy {
    private final APIClient apiClient;

    public PopularityBasedStrategy(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Track> recommend(List<Track> userTracks, int count) {
        try {
            // 1️⃣ Ensure there’s at least one track to use as a seed
            if (userTracks.isEmpty()) {
                return new ArrayList<>();
            }

            // 2️⃣ Use the first track’s artist as the seed
            Track seedTrack = userTracks.get(0);
            if (seedTrack.getArtists().isEmpty()) {
                return new ArrayList<>();
            }

            String artistName = seedTrack.getArtists().get(0);

            // 3️⃣ Search Spotify for the artist to get their ID
            List<Artist> artists = apiClient.searchArtistByName(artistName);
            if (artists.isEmpty()) {
                System.err.println("No artist found for: " + artistName);
                return new ArrayList<>();
            }

            String artistId = artists.get(0).getId();

            // 4️⃣ Fetch top tracks for that artist
            return apiClient.getTopTracksForArtist(artistId, "US");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public String getStrategyName() {
        return "Popularity-Based Recommendations";
    }
}
