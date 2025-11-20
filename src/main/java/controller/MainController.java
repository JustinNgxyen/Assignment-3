package controller;

import client.ServerConnection;
import model.RecommendationEngine;
import model.FeatureBasedStrategy;
import model.Track;
import model.APIClient;
import view.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class MainController {

    private final MainFrame view;
    private final ServerConnection serverConnection;
    private final RecommendationEngine recommendationEngine;
    private final APIClient apiClient;

    public MainController(MainFrame view) {
        this.view = view;
        this.serverConnection = new ServerConnection();
        this.apiClient = new APIClient();

        try {
            System.out.println("Authenticating Spotify API client...");
            apiClient.authenticate();
        } catch (Exception e) {
            System.err.println("Failed to authenticate APIClient: " + e.getMessage());
        }

        // Use APIService
        this.recommendationEngine = new RecommendationEngine(new FeatureBasedStrategy(apiClient));

        initializeServerConnection();
        initializeListeners();
    }

    private void initializeServerConnection() {
        try {
            serverConnection.connect();
            view.updateStatus("Connected to server");
        } catch (IOException e) {
            view.showError("Failed to connect to server: " + e.getMessage());
            view.updateStatus("Not connected");
        }
    }

    private void initializeListeners() {
        view.setOnSearchListener(this::performSearch);
        view.setOnRecommendationListener(this::getRecommendations);
    }

    public void performSearch(String query) {
        if (query == null || query.isEmpty()) {
            view.showError("Please enter a search query");
            return;
        }

        view.updateStatus("Searching for: " + query);

        SwingWorker<List<Track>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Track> doInBackground() throws Exception {
                return serverConnection.searchTracks(query);
            }

            @Override
            protected void done() {
                try {
                    List<Track> results = get();
                    view.displaySearchResults(results);
                    view.updateStatus("Found " + results.size() + " tracks");
                } catch (Exception e) {
                    view.showError("Search failed: " + e.getMessage());
                    view.updateStatus("Search failed");
                }
            }
        };
        worker.execute();
    }

    private void getRecommendations(Track selectedTrack) {
        if (selectedTrack == null) {
            view.showError("Please select a track first");
            return;
        }

        view.updateStatus("Getting feature-based recommendations for: " + selectedTrack.getName());

        SwingWorker<List<Track>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Track> doInBackground() throws Exception {
                // ✅ Use the FeatureBasedStrategy directly (uses APIService)
                FeatureBasedStrategy strategy = new FeatureBasedStrategy(apiClient);
                List<Track> userTracks = List.of(selectedTrack);
                return strategy.recommend(userTracks, 10);
            }

            @Override
            protected void done() {
                try {
                    List<Track> recommendations = get();
                    view.displayRecommendations(recommendations);
                    view.updateStatus("Generated " + recommendations.size() + " recommendations");
                } catch (Exception e) {
                    view.showError("Failed to get recommendations: " + e.getMessage());
                    view.updateStatus("Recommendation failed");
                }
            }
        };
        worker.execute();
    }

}
