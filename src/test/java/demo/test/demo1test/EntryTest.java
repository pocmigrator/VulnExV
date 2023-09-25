package demo.test.demo1test;

import demo.test.demo1.Component1;
import demo.test.demo1.Entry;
import demo.test.demo1.User;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

public class EntryTest {

    @Test
    public void testVulnerabilityIsTriggered() {
        // Arrange
        User user = User.builder().name("John Doe").build();
        int number = 100;

        Component1 c1 = Mockito.spy(new Component1());
        Entry entry = new Entry();

        // Act
        entry.entryMethod(user, number);

        // Assert
        verify(c1).functionC1(number, "a");
    }

    @Test
    public void testEntryMethodVulnerability23() {
        // Create a mock instance of Component1
        Component1 mockComponent1 = Mockito.mock(Component1.class);

        // Create an instance of Entry
        Entry entry = new Entry();

        // Define the input data
        int number = 100;
        String inputStr = "a";

        // Call the entryMethod, which internally calls functionC1
        entry.entryMethod(new User("TestUser"), number);

        // Verify that functionC1 was called with the specified input
        verify(mockComponent1).functionC1(number, inputStr);
    }
    @Test
    public void testVulnerabilityTriggered() {
        // Arrange
        int inputNumber = 100;
        Component1 component1 = Mockito.spy(new Component1());
        Entry entry = new Entry();

        // Act
        entry.entryMethod(new User(), inputNumber);

        // Assert
        verify(component1).functionC1(inputNumber, "");
    }

    @Test(expected = RuntimeException.class)
    public void testEntryMethodVulnerability() {
        Entry entry = new Entry();
        int inputNumber = 235;
        entry.entryMethod(null, inputNumber);
    }


    @Test(expected = RuntimeException.class)
    public void testEntryMethodVulnerability2() {
        Entry entry = new Entry();
        int inputNumber = 20;
        User user = User.builder().name("John").build(); // Create a User object with a name
        entry.entryMethod(user, inputNumber);
    }
}