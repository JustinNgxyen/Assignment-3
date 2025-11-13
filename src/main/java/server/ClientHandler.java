package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.APIClient;
import model.Track;
import model.Artist;
import org.apache.hc.core5.http.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Handles individual client connections in separate threads
 * Demonstrates Multithreading and Socket communication
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final APIClient apiClient;
    private final Gson gson;

    public ClientHandler(Socket socket, APIClient apiClient) {
        this.clientSocket = socket;
        this.apiClient = apiClient;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        System.out.println("New client connected: " + clientSocket.getInetAddress());

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request;
            while ((request = in.readLine()) != null) {

                if (request.trim().isEmpty()) {
                    // Skip empty lines so Gson never sees "null"
                    continue;
                }

                System.out.println("Received request: " + request);
                String response = handleRequest(request);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Process client requests and return JSON responses
     */
    private String handleRequest(String request) {
        try {
            if (request == null || request.trim().isEmpty()) {
                return createErrorResponse("Empty or invalid request");
            }

            JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);
            if (jsonRequest == null || !jsonRequest.has("action")) {
                return createErrorResponse("Not a valid JSON Object: " + request);
            }

            String action = jsonRequest.get("action").getAsString();

            switch (action) {
                case "SEARCH":
                    return handleSearch(jsonRequest);
                case "RECOMMEND":
                    return handleRecommend(jsonRequest);
                default:
                    return createErrorResponse("Unknown action: " + action);
            }
        } catch (Exception e) {
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }


    private String handleSearch(JsonObject request) {
        try {
            String query = request.get("query").getAsString();
            int limit = request.has("limit") ? request.get("limit").getAsInt() : 10;

            List<Track> tracks = apiClient.searchTracks(query, limit);

            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            response.addProperty("action", "SEARCH");
            response.add("data", gson.toJsonTree(tracks));

            return gson.toJson(response);
        } catch (IOException e) {
            return createErrorResponse("Search failed: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleRecommend(JsonObject request) {
        try {
            String trackId = request.get("trackId").getAsString();
            String market = "US"; // You can change this for your region

            // ✅ 1. Get full track details by ID
            Track seedTrack = apiClient.getTrackById(trackId);

            if (seedTrack == null || seedTrack.getArtists().isEmpty()) {
                return createErrorResponse("No artist found for track ID: " + trackId);
            }

            // ✅ 2. Use the first artist of the selected track
            String artistName = seedTrack.getArtists().get(0);
            System.out.println("Getting top tracks for artist: " + artistName);

            // ✅ 3. Search Spotify for the artist to get their ID
            List<Artist> artistMatches = apiClient.searchArtistByName(artistName);
            if (artistMatches.isEmpty()) {
                return createErrorResponse("Artist not found: " + artistName);
            }

            String artistId = artistMatches.get(0).getId();

            // ✅ 4. Get top tracks for that artist
            List<Track> topTracks = apiClient.getTopTracksForArtist(artistId, market);

            // ✅ 5. Package results into a JSON response
            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            response.addProperty("action", "RECOMMEND");
            response.add("data", gson.toJsonTree(topTracks));

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("Recommendation failed: " + e.getMessage());
        }
    }

    private String createErrorResponse(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "error");
        response.addProperty("message", message);
        return gson.toJson(response);
    }
}