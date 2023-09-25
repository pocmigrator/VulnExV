package demo.test.chain;

/**
 *
 * 					f1
 *
 * 		F2  						f3			f8
 *
 * F4 	f5 	f6							        f9	f10
 *
 * 						f7
 */
public class Demo1 {
    Object a = new Object();
    public void func1_2(){
        func2();
    }

    public void func1_3(){
        func3();
    }
    public void func1(){
        func2();
        func3();
        func8();
    }
    public void func2(){
        func4();
        func5();
        func6();
    }

    public void func3(){
    }
    public void func4(){
        a.notify();
    }
    public void func5(){
        a.notify();
    }
    public void func6(){
        func7();
    }
    public void func7(){
        a.notify();
    }
    public void func8(){
        func9();
        func10();
    }
    public void func9(){
        a.notify();
    }
    public void func10(){
        a.notify();
    }















}
