package model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class testAuthenticationSetsAccessToken {
    @Test
    void testAuthenticationAccessToken() throws Exception {
        APIClient client = new APIClient();
        client.authenticate();
        Field token = APIClient.class.getDeclaredField("accessToken");
        token.setAccessible(true);
        assertNotNull(token.get(client));
    }

}
