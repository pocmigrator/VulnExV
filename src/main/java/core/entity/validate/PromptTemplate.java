package core.entity.validate;

public interface PromptTemplate {
//    reference types, and Mockito framework if necessary
    String hint1 = "I want you to generate a unit test, using the JUnit testing framework, \n" +
//            "And using the Mockito framework to verify that the vulnerability method is called for a given input, \n" +
            "This test is used to verify that the vulnerability was successfully triggered,\n" +
            "You can't have non-existent method calls in the results you give,\n" +
            "Just generate a single test method,\n" +
            "For fuzzy parameters, give specific commonly used types, such as Object,String and so on,\n"+
            "Only the function under test statement is wrapped by a try catch statement(Excluding assert statement), the Exception class uses an exception, and does nothing after the catch,\n"+
            "The code corresponds to the function being tested, with context information, for example: ";
    String hint2 = "The source code of the function under test is (The unit test should test this function):";
    String hint3 = "and the class name of the function under test is:";
    String hint4 = "For reference type parameters of the function under test, they are defined as: ";
    String hint5 = "There is a method call chain from the function under test to the function called by the vulnerability code, the list of functions is:";
    String hint6 = "Where there is a vulnerability code call function source code is (This function is used to help you understand):";
    String hint7 = "and the class name of this function is:";

    String hint8 = "The vulnerable third-party library function and the class to which the function belongs are:";
    String hint9 = "The input variable name for this unit test is \"input\", and the value of this \"input\" is:";

    String hint10 = "After declaring test input data, you need to call the following statement:";

    String hint11 = "The assert statement to verify that the vulnerability was successfully triggered is fixed as:\n" +
            "assertTrue(MethodCallInterceptor.isTrigger);";
}

