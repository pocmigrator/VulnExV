package demo.test.demo1test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TMock.BClass.class})
public class TMock {

    @Test
    public void testAdd() {
        int result = MathUtils.add(2, 3);

        Thread thread = Thread.currentThread();
        StackTraceElement[] stackTrace = thread.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            System.out.println(stackTrace[i].getMethodName());
        }
//        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//        boolean addMethodCalled = false;
//        for (StackTraceElement element : stackTrace) {
//            if (element.getClassName().equals(MathUtils.class.getName()) &&
//                    element.getMethodName().equals("add")) {
//                addMethodCalled = true;
//                break;
//            }
//        }

//        assertEquals(5, result);
//        assertTrue(addMethodCalled);
    }



    @Test
    public void testFunc1CallsFunc3WithoutInjectin2g() throws Exception {
        // Mock BClass, because it's calling CClass
        BClass bMock = PowerMockito.mock(BClass.class);

        // Create an instance of AClass
        AClass a = new AClass();

        // Call func1
        a.func1("test");

        // Verify that CClass's func3 was called
        PowerMockito.verifyPrivate(bMock, Mockito.times(1)).invoke("func2", "test");
    }


    @Test
    public void testFunc1CallsFunc3WithoutInjecting() throws Exception {
        // Mock BClass, because it's calling CClass
        BClass bMock = PowerMockito.mock(BClass.class);

        // Create an instance of AClass
        AClass a = new AClass();

        // Call func1
        a.func1("test");

        // Verify that CClass's func3 was called
        PowerMockito.verifyPrivate(bMock).invoke("func3", Mockito.anyString());
    }

    @Test
    public void testFunc1CallsFunc3WithSpy() {
        // Create a spy object for CClass
        CClass cSpy = Mockito.spy(new CClass());

        // Create an instance of AClass
        AClass a = new AClass();

        // Call func1
        a.func1("test");

        // Verify that func3 was called on the spy
        Mockito.verify(cSpy, Mockito.times(1)).func3("test");
    }


    @Test
    public void testFunc1CallsFunc3_2() {
        // Create a mock for CClass
        CClass cMock = Mockito.mock(CClass.class);

        // Create an instance of AClass
//        AClass a = new AClass(cMock);

        // Use reflection to call func1 on AClass
//        a.func1("2");

        // Verify that func3 was called with the expected argument
        Mockito.verify(cMock, Mockito.times(1)).func3("2");
    }

    @Test
    public void testFunc1CallsFunc3() throws Exception {
        // Create a mock for CClass
        CClass cMock = Mockito.mock(CClass.class);

        // Create an instance of AClass
//        AClass a = new AClass();

        // Use reflection to call func1 on AClass
        Method func1Method = AClass.class.getDeclaredMethod("func1", String.class);
        func1Method.setAccessible(true); // Allow access to private method
//        func1Method.invoke(a, "test");

        // Verify that func3 was called
        Mockito.verify(cMock, Mockito.times(1)).func3("2");
    }


    public static class AClass{
        public void func1(String a){
            BClass b = new BClass();
            b.func2(a);
        }
    }
    public static class BClass{
        public void func2(String a){
            CClass c = new CClass();
            c.func3(a);
        }
    }

    public static class CClass{
        public void func3(String a){
            System.out.println(a);
        }

        public void func4(String a){
            System.out.println(a);
        }

        public void func54(int a){
            System.out.println(a);
        }
    }

}
