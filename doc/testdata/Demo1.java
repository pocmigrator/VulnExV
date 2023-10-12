public class Demo1{
    public void vulnerableMethod1(String data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(data, Object.class);
    }

    public void vulnerableMethod2(String input) {
        String data = process(input);
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(data, Object.class);
    }


    public void vulnerableMethod3() {
        String data = getFromDatabase();
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(data, Object.class);
    }

    public void vulnerableMethod4() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(data.substring(11,22), Object.class);
    }
}