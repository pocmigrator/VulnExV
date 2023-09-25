import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FileReaderTest {

    @Test
    public void testVulnerabilityTriggered() throws Exception {
        // Test input data
        String filename = "\\foo\\.\\bar";
        String splitPunc = " ";

        try {
            // Intercept the method call to FilenameUtils.normalize
            MethodCallInterceptor.interceptor(
                org.apache.commons.io.FilenameUtils.class, "normalize",
                new Object[]{filename}
            );

            // Vulnerable code under test
            new FileReader(filename, splitPunc);
        } catch (Exception e) {
            // Exception handling, if needed
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
