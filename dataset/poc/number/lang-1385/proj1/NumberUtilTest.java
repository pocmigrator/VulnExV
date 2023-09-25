import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class NumberUtilTest {

    @Test
    public void testVulnerabilityTriggered() {
        // Test input data
        String input = "L";

        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            org.apache.commons.lang3.math.NumberUtils.class,
            "createNumber",
            new Object[]{input}
        );

        try {
            // Call the createNumber method in NumberUtil
            NumberUtil.createNumber(input);
        } catch (Exception e) {
            // Exception caught, do nothing
        }

        // Verify that the vulnerability was triggered by checking MethodCallInterceptor.isTrigger
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
