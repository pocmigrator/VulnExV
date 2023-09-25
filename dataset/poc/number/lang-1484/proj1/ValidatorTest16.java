import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class ValidatorTest {

    @Test
    public void testVulnerabilityTriggered() {
        // Test input data
        String input = "100.";

        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            org.apache.commons.lang3.math.NumberUtils.class,
            "isParsable",
            new Object[]{input}
        );

        // Create a Validator object and call the validateShort method
        Validator validator = new Validator();
        boolean result = validator.validateShort(input);

        // Verify that the vulnerability was triggered by checking MethodCallInterceptor.isTrigger
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
