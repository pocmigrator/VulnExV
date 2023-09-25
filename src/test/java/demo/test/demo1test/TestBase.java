package demo.test.demo1test;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class TestBase {
    @Test
    public  void test(){
        Base64.decodeBase64("publishMessage");
    }
}
