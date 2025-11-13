package service;

import com.google.gson.*;
import model.Track;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles communication with the Spotify Web API using Gson and HttpClient 5
 */
public class APIService {

    private static final String BASE_URL = "https://api.spotify.com/v1/";
    private static final String AUTH_TOKEN = "YOUR_SPOTIFY_ACCESS_TOKEN"; // TODO: replace with a valid token

    private final HttpClient httpClient;
    private final Gson gson;

    public APIService() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
    }

    /** Search tracks by name */
    public List<Track> searchTracks(String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = BASE_URL + "search?q=" + encoded + "&type=track&limit=10";
        String json = makeGetRequest(url);

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray items = root.getAsJsonObject("tracks").getAsJsonArray("items");

        return parseTracks(items);
    }

    /** Get recommendations based on a seed track */
    public List<Track> getRecommendations(String seedTrackId) throws Exception {
        String url = BASE_URL + "recommendations?seed_tracks=" + seedTrackId + "&limit=10";
        String json = makeGetRequest(url);

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray items = root.getAsJsonArray("tracks");

        return parseTracks(items);
    }

    /** Perform authorized GET request */
    private String makeGetRequest(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + AUTH_TOKEN);
        request.setHeader("Accept", "application/json");

        return httpClient.execute(request, response ->
                EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
        );
    }

    /** Parse JSON into Track objects */
    private List<Track> parseTracks(JsonArray items) {
        List<Track> tracks = new ArrayList<>();

        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();

            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();

            // Artists list
            List<String> artists = new ArrayList<>();
            for (JsonElement a : obj.getAsJsonArray("artists")) {
                artists.add(a.getAsJsonObject().get("name").getAsString());
            }

            String albumName = obj.getAsJsonObject("album").get("name").getAsString();
            int durationMs = obj.get("duration_ms").getAsInt();
            int popularity = obj.has("popularity") ? obj.get("popularity").getAsInt() : 0;
            String previewUrl = obj.has("preview_url") && !obj.get("preview_url").isJsonNull()
                    ? obj.get("preview_url").getAsString() : null;

            // Use your Track model constructor
            Track track = new Track(id, name, artists, albumName);
            track.setDurationMs(durationMs);
            track.setPopularity(popularity);
            track.setPreviewUrl(previewUrl);

            tracks.add(track);
        }

        return tracks;
    }
}
