package demo.test.demo1test;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ConsoleCaptureExample {
    public static void main(String[] args) {
        int result = MathUtils.add(2, 3);
        int result2 = MathUtils.add(2, 3);
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[1];
//        StackTraceElement stackTraceElement1 = Thread.currentThread().getStackTrace()[2];
        StackTraceElement[] temp=Thread.currentThread().getStackTrace();
        StackTraceElement a=(StackTraceElement)temp[2];
        System.out.println("----from--"+a.getMethodName()+"--method----------to use-refreshcart--------");

    }

    public static void main33(String[] args) {
        try {
            int result = MathUtils.add(2, 3);
            throw new Exception("这是一个示例异常");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                System.out.println("d "+element.toString());
            }
        }

    }

    public static void main222(String[] args) {
        int result = MathUtils.add(2, 3);

        Throwable throwable = new Throwable();
        StackTraceElement[] elements = throwable.getStackTrace();
        for (StackTraceElement element : elements) {
            System.out.println(element.getClassName() + " - " + element.getMethodName() + " - " + element.getLineNumber());
        }

    }
    public static void main2(String[] args) {
        int result = MathUtils.add(2, 3);

        Throwable throwable = new Throwable();
        StackTraceElement[] elements = throwable.getStackTrace();
        for (StackTraceElement element : elements) {
            System.out.println(element.getClassName() + " - " + element.getMethodName() + " - " + element.getLineNumber());
        }

        // 创建一个字节数组输出流，用于捕获输出内容
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 创建一个自定义的PrintStream，将输出重定向到字节数组输出流
        PrintStream customPrintStream = new PrintStream(outputStream);

        // 将标准输出流重定向到自定义的PrintStream
        System.setOut(customPrintStream);

        // 执行一些输出操作
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

        allStackTraces.values().forEach(x->{
            Arrays.stream(x)
                    .filter(Objects::nonNull)
                    .forEach(y->{
                        System.out.println("xx:"+y);
                    });

        });


//        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//        for (StackTraceElement element : elements) {
//            System.out.println(element.getClassName() + " - " + element.getMethodName() + " - " + element.getLineNumber());
//        }
        // 恢复标准输出流
//        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
//
//        // 获取捕获的输出内容并打印
//        String capturedOutput = outputStream.toString();
//        System.out.println("Captured Output:");
//        System.out.println(capturedOutput);
    }


    public static void printStackTrace() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : elements) {
            System.out.println(element.getClassName() + " - " + element.getMethodName() + " - " + element.getLineNumber());
        }
    }
}