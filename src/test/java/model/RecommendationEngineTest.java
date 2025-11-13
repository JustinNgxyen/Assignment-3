package model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecommendationEngineTest {
    @Test
    void testRecommendationEngineUsesStrategy() {
        APIClient client = new APIClient();
        RecommendationEngine engine = new RecommendationEngine(new FeatureBasedStrategy(client));
        List<Track> recs = engine.getRecommendations(List.of(new Track("1", "Test", List.of("Queen"), "Album")), 5);
        assertNotNull(recs);
    }

}
