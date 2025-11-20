package model;

/**
 * Represents audio analysis features from Spotify API
 */
public class AudioFeatures {
    private String trackId;
    private double danceability;
    private double energy;
    private double valence;
    private double tempo;
    private double acousticness;

    public AudioFeatures(String trackId) {
        this.trackId = trackId;
    }

    // Setters
    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }
}