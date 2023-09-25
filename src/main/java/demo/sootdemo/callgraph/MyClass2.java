package demo.sootdemo.callgraph;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyClass2 {

    public void func1(){
        func2();
    }

    private void func2() {
        vulnerableMethod("abc");
    }

    private void vulnerableMethod(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(data, Object.class); // 此处的data是漏洞输入
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String basePath = "src/main/java"; // 替换为你项目中的实际路径

        List<String> packages = getAllPackages(basePath);
        for (String packageName : packages) {
            System.out.println(packageName);
        }

        String classpath = System.getProperty("java.class.path");
        System.out.println(classpath);
    }

    public static List<String> getAllPackages(String basePath) {
        List<String> packages = new ArrayList<>();
        File srcDir = new File(basePath);

        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return packages;
        }

        scanPackages(srcDir, "", packages);
        return packages;
    }

    private static void scanPackages(File dir, String packageName, List<String> packages) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String newPackageName = packageName + "." + file.getName();
                    scanPackages(file, newPackageName, packages);
                }
            }
        }

        if (!packageName.isEmpty()) {
            packages.add(packageName.substring(1)); // 去除开头的点号
        }
    }
}
