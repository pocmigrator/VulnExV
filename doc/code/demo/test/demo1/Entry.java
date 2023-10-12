package demo.test.demo1;

public class Entry {

    public void entryMethod(User user, int number){
        Component1 c1 = new Component1();
        c1 . functionC1(number,  "a");
    }

    public void entryMethod2(String abc) {
        Component1 c1 = new Component1();
        String userInput = c1.getUserInput();
        c1.vulnerableMethod(userInput);
    }
}
