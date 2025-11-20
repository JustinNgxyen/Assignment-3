package model;

import java.util.List;

/**
 * Represents a music track with encapsulated properties.
 * Demonstrates Encapsulation - private fields with public getters/setters
 */
public class Track {
    private String id;
    private String name;
    private List<String> artists;
    private String albumName;
    private int durationMs;
    private int popularity;
    private String previewUrl;

    // Constructor
    public Track(String id, String name, List<String> artists, String albumName) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.albumName = albumName;
    }

    // Getters and Setters (Encapsulation)
    public String getName() {
        return name;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)",
                String.join(", ", artists), name, albumName);
    }

    // Convert duration to readable format
    public String getFormattedDuration() {
        int seconds = durationMs / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}