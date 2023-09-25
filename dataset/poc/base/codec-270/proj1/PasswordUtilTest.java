import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class PasswordUtilTest {

    @Test
    public void testVulnerabilityTriggered() {
        // Test input data
        String publishMessage = "encryptedPassword"; // 此处应该是 publishMessage

        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            org.apache.commons.codec.binary.Base64.class,
            "decodeBase64",
            new Object[]{publishMessage} // 此处也应该是 publishMessage
        );

        try {
            // Call the function under test
            PasswordUtil.decrypt(publishMessage, salt);
        } catch (Exception e) {
            // Exception caught, do nothing
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
