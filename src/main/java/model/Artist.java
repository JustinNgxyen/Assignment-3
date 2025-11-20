package model;

import java.util.List;

public class Artist {
    private String id;
    private String name;
    private List<String> genres;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}