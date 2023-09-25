import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class MoneyFormatterTest {

    @Test
    public void testVulnerabilityTriggered() throws ParseException {
        // Test input data
        String input = "100.";

        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            org.apache.commons.lang3.math.NumberUtils.class,
            "isParsable",
            new Object[]{input}
        );

        // Create a MoneyFormatter object and call the parse method
        MoneyFormatter moneyFormatter = new MoneyFormatter();
        Locale locale = Locale.getDefault();
        try {
            moneyFormatter.parse(input, locale);
        } catch (ParseException e) {
            // Exception caught, do nothing
        }

        // Verify that the vulnerability was triggered by checking MethodCallInterceptor.isTrigger
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
