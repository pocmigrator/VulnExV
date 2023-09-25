import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FileResourceLoaderTest {

    @Test
    public void testVulnerabilityTriggered() throws ResourceNotFoundException {
        // Test input data
        String templateName = "\\foo\\.\\bar";
        String encoding = "UTF-8";

        try {
            // Intercept the method call to FilenameUtils.normalize
            MethodCallInterceptor.interceptor(
                org.apache.commons.io.FilenameUtils.class, "normalize",
                new Object[]{templateName}
            );

            // Vulnerable code under test
            new FileResourceLoader().getResourceReader(templateName, encoding);
        } catch (Exception e) {
            // Exception handling, if needed
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
