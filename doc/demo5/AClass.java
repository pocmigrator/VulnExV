public class AClass{
    public void func1(String a){
        BClass b = new BClass();
        b.func2("1");
    }
}
public class BClass{
    public void func2(String a){
        CClass c = new CClass();
        c.func3("2");
    }
}

public class CClass{
    public void func3(String a){
        System.out.println("a");
    }
}


public class DClass{

    public void func4(String a){

    }

}
