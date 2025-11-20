package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.config;
import model.Track;
import model.Artist;
import model.AudioFeatures;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * REST API Client for Spotify Web API
 * Demonstrates REST API calls and JSON parsing
 */
public class APIClient {
    private String accessToken;
    private final CloseableHttpClient httpClient;

    public APIClient() {
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Authenticate with Spotify API using Client Credentials Flow
     * Demonstrates REST POST request
     */
    public void authenticate() throws IOException, ParseException {
        String auth = config.CLIENT_ID + ":" + config.CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpPost httpPost = new HttpPost(config.TOKEN_URL);
        httpPost.setHeader("Authorization", "Basic " + encodedAuth);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setEntity(new StringEntity("grant_type=client_credentials"));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            this.accessToken = jsonObject.get("access_token").getAsString();
            System.out.println("Successfully authenticated with Spotify API");
            System.out.println("Access Token: " + accessToken);

        }
    }

    /**
     * Search for tracks by query
     * Demonstrates REST GET request and JSON parsing
     */
    public List<Track> searchTracks(String query, int limit) throws IOException, ParseException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/search?q=%s&type=track&limit=%d",
                config.API_BASE_URL, encodedQuery, limit);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            return parseTracksFromJson(jsonResponse);
        }
    }

    /**
     * Parse Track objects from JSON response
     * Demonstrates JSON parsing and object creation
     */
    private List<Track> parseTracksFromJson(String jsonResponse) {
        List<Track> tracks = new ArrayList<>();
        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray items = root.getAsJsonObject("tracks").getAsJsonArray("items");

        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();

            // Extract track information
            String id = item.get("id").getAsString();
            String name = item.get("name").getAsString();

            // Extract artists
            List<String> artists = new ArrayList<>();
            JsonArray artistsArray = item.getAsJsonArray("artists");
            for (int j = 0; j < artistsArray.size(); j++) {
                artists.add(artistsArray.get(j).getAsJsonObject().get("name").getAsString());
            }

            // Extract album name
            String albumName = item.getAsJsonObject("album").get("name").getAsString();

            // Create Track object
            Track track = new Track(id, name, artists, albumName);
            track.setDurationMs(item.get("duration_ms").getAsInt());
            track.setPopularity(item.get("popularity").getAsInt());

            if (!item.get("preview_url").isJsonNull()) {
                track.setPreviewUrl(item.get("preview_url").getAsString());
            }

            tracks.add(track);
        }

        return tracks;
    }

    /**
     * Gets top tracks of artist based on seed tracks
     */
    public List<Track> getTopTracksForArtist(String artistId, String market) throws IOException {
        String url = String.format("%s/artists/%s/top-tracks?market=%s", config.API_BASE_URL, artistId, market);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray tracksArray = root.getAsJsonArray("tracks");
            return parseTracksFromJsonArray(tracksArray);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Artist> searchArtistByName(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/search?q=%s&type=artist&limit=3", config.API_BASE_URL, encodedQuery);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();

            // 🧠 Handle error responses gracefully
            if (root.has("error")) {
                JsonObject error = root.getAsJsonObject("error");
                String message = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                System.err.println("⚠️ Spotify API error: " + message);
                return new ArrayList<>();
            }

            JsonObject artistsObj = root.getAsJsonObject("artists");
            if (artistsObj == null || !artistsObj.has("items")) {
                System.err.println("⚠️ No 'artists' object or 'items' array in response: " + responseBody);
                return new ArrayList<>();
            }

            JsonArray items = artistsObj.getAsJsonArray("items");
            List<Artist> artists = new ArrayList<>();

            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();
                String id = item.get("id").getAsString();
                String name = item.get("name").getAsString();
                artists.add(new Artist(id, name));
            }

            return artists;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Track> parseTracksFromJsonArray(JsonArray tracksArray) {
        List<Track> tracks = new ArrayList<>();

        for (int i = 0; i < tracksArray.size(); i++) {
            JsonObject item = tracksArray.get(i).getAsJsonObject();

            String id = item.get("id").getAsString();
            String name = item.get("name").getAsString();

            List<String> artists = new ArrayList<>();
            JsonArray artistsArray = item.getAsJsonArray("artists");
            for (int j = 0; j < artistsArray.size(); j++) {
                artists.add(artistsArray.get(j).getAsJsonObject().get("name").getAsString());
            }

            String albumName = item.getAsJsonObject("album").get("name").getAsString();

            Track track = new Track(id, name, artists, albumName);
            track.setDurationMs(item.get("duration_ms").getAsInt());
            track.setPopularity(item.get("popularity").getAsInt());

            tracks.add(track);
        }

        return tracks;
    }

    /**
     * Get detailed information about a track by its Spotify ID.
     */
    public Track getTrackById(String trackId) throws IOException, ParseException {
        String url = String.format("%s/tracks/%s", config.API_BASE_URL, trackId);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject item = JsonParser.parseString(responseBody).getAsJsonObject();

            // ✅ Extract track data
            String id = item.get("id").getAsString();
            String name = item.get("name").getAsString();

            // Extract artist names
            List<String> artists = new ArrayList<>();
            JsonArray artistsArray = item.getAsJsonArray("artists");
            for (int j = 0; j < artistsArray.size(); j++) {
                artists.add(artistsArray.get(j).getAsJsonObject().get("name").getAsString());
            }

            // Album name
            String albumName = item.getAsJsonObject("album").get("name").getAsString();

            // Create Track object
            Track track = new Track(id, name, artists, albumName);
            track.setDurationMs(item.get("duration_ms").getAsInt());
            track.setPopularity(item.get("popularity").getAsInt());

            if (!item.get("preview_url").isJsonNull()) {
                track.setPreviewUrl(item.get("preview_url").getAsString());
            }

            return track;
        }
    }

    public void close() throws IOException {
        httpClient.close();
    }
}