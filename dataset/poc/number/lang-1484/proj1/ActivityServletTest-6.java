import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActivityServletTest {

    @Test
    public void testVulnerabilityTriggered() throws ServletException, IOException {
        // Test input data
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String requestedId = "100.";

        // Set up the mocked HttpServletRequest
        when(req.getServletPath()).thenReturn("/admin/activity");
        when(req.getParameter("id")).thenReturn(requestedId);

        // Intercept the vulnerable method call
        MethodCallInterceptor.interceptor(
            org.apache.commons.lang3.math.NumberUtils.class,
            "isParsable",
            new Object[]{requestedId}
        );

        // Create an ActivityServlet object and call the doGet method
        ActivityServlet servlet = new ActivityServlet();
        servlet.doGet(req, resp);

        // Verify that the vulnerability was triggered by checking MethodCallInterceptor.isTrigger
        assertTrue(MethodCallInterceptor.isTrigger);

        // Verify that PrintWriter is closed after the catch
        verify(writer).close();
    }
}
