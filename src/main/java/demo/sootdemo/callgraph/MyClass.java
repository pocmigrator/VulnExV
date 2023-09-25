package demo.sootdemo.callgraph;

// MyClass.java
public class MyClass {
    public static void main(String[] args) {
        methodA();
    }

    public static void methodA() {
        methodB();
    }

    public static void methodB() {
        System.out.println("Hello from methodB!");

        MyClass2 myClass2 = new MyClass2();
        myClass2.func1();
    }
}