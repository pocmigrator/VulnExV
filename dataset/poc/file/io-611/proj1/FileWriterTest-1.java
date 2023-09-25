import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FileWriterTest {

    @Test
    public void testVulnerabilityTriggered() {
        // Test input data
        String input = "\\foo\\.\\bar";

        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            org.apache.commons.io.FilenameUtils.class,
            "normalize",
            new Object[]{input}
        );

        try {
            // Create an instance of FileWriter and call the function under test
            FileWriter fileWriter = new FileWriter(input, true); // Or provide the appropriate overwrite value
        } catch (Exception e) {
            // Exception caught, do nothing
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
