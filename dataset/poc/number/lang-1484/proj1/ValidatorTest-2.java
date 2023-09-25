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

        try {
            // Create a Validator object and call the function under test
            Validator validator = new Validator();
            validator.validateInteger(input);
        } catch (Exception e) {
            // Exception caught, do nothing
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
