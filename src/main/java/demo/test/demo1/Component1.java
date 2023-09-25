package demo.test.demo1;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Component1 {

    public void functionC1(int num, String str)  {
        System.out.println("functionC1"+str);

        Object a = new Object();
        // vul code
        try {
            a.wait(num,   1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public void vulnerableMethod(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(data, Object.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getUserInput() {
        return "[]";
    }

}
