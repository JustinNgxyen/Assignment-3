package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class FeatureBasedStrategyTest {
    @Test
    void testFeatureBasedStrategyReturnsTopTracks() throws Exception {
        APIClient client = new APIClient();
        client.authenticate();
        FeatureBasedStrategy strategy = new FeatureBasedStrategy(client);
        Track seed = client.searchTracks("The Beatles", 1).get(0);
        List<Track> recs = strategy.recommend(List.of(seed), 5);
        assertFalse(recs.isEmpty());
    }

}
