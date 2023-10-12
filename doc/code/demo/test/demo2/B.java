package demo.test.demo2;

public class B extends A{

    @Override
    public void func(){
        Object a = new Object();
        try {
            a.wait(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
