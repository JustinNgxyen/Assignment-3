package model;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple artist-based recommendation strategy.
 * Uses the artist of the selected track and returns their top tracks.
 */
public class FeatureBasedStrategy implements RecommendationStrategy {

    private final APIClient apiClient;

    public FeatureBasedStrategy(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Track> recommend(List<Track> userTracks, int count) {
        if (userTracks == null || userTracks.isEmpty()) {
            System.err.println("⚠️ No seed tracks provided for recommendation.");
            return new ArrayList<>();
        }

        try {
            // 1️⃣ Use the first track as the seed
            Track seedTrack = userTracks.get(0);
            if (seedTrack.getArtists().isEmpty()) {
                System.err.println("⚠️ No artist found for seed track.");
                return new ArrayList<>();
            }

            // 2️⃣ Search the artist by name to get the Spotify artist ID
            String artistName = seedTrack.getArtists().get(0);
            List<Artist> artistMatches = apiClient.searchArtistByName(artistName);
            if (artistMatches.isEmpty()) {
                System.err.println("⚠️ Artist not found: " + artistName);
                return new ArrayList<>();
            }

            String artistId = artistMatches.get(0).getId();

            // 3️⃣ Fetch that artist’s top tracks
            List<Track> topTracks = apiClient.getTopTracksForArtist(artistId, "US");

            // 4️⃣ Limit the results to 'count'
            if (topTracks.size() > count) {
                return topTracks.subList(0, count);
            }
            return topTracks;

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public String getStrategyName() {
        return "Artist-Based Top Tracks";
    }
}
