import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FileResourceLoaderTest {

    @Test
    public void testVulnerabilityTriggered() {
        // Test input data
        String name = "\\foo\\.\\bar";

        try {
            // Intercept the method call to FilenameUtils.normalize
            MethodCallInterceptor.interceptor(
                org.apache.commons.io.FilenameUtils.class, "normalize",
                new Object[]{name}
            );

            // Vulnerable code under test
            new FileResourceLoader().resourceExists(name);
        } catch (Exception e) {
            // Exception handling, if needed
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
