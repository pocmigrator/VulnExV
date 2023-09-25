import core.entity.analysis.PClass;
import org.junit.Test;

public class TestClass {


    @Test
    public void testClassName() throws Exception {
        PClass a = new PClass();

        String name = PClass.class.getPackage().getName();
        System.out.println(PClass.class.getName());
        System.out.println(PClass.class.getSimpleName());
        System.out.println(PClass.class.getTypeName());
        System.out.println(PClass.class.toGenericString());

        PClass b = (PClass)Class.forName(name + ".PClass").newInstance();

        System.out.println(b.toString());
    }


}
