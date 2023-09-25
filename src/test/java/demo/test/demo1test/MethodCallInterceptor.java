package demo.test.demo1test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Arrays;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class MethodCallInterceptor {
    public static boolean isTrigger = false;

    public static void main(String[] args) {
        String input="hello";
        ByteBuddyAgent.install();
        interceptMethodCalls(TMock.CClass.class, "func3", new String[]{input});

        TMock.AClass aClass = new TMock.AClass();
        aClass.func1(input);

        System.out.println(MethodCallInterceptor.isTrigger);
    }


    public static void interceptor(Class thdClass, String methodName, Object[] args){
        ByteBuddyAgent.install();
        interceptMethodCalls(thdClass, methodName, args);
    }

    public static void interceptMethodCalls(Class<?> aClass, String methodName, Object[] args) {
        MethodCallLogger.targetMethodName = methodName;
        MethodCallLogger.targetArgs = args;
        try {
            if (!aClass.getName().contains("ByteBuddy")) {  // 避免重复加载
                DynamicType.Loaded<?> load = new ByteBuddy()
                        .redefine(aClass)  // 拦截 AClass
                        .visit(Advice.to(MethodCallLogger.class).on(named(methodName)))
                        .make()
                        .load(aClass.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MethodCallLogger {
        public static String targetMethodName;
        public static Object[] targetArgs;


        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin String methodName, @Advice.AllArguments Object[] args) {
            System.out.println("targetMethodName: "+targetMethodName);
            System.out.println("targetArgs:");
            for (int i = 0; i < targetArgs.length; i++) {
                System.out.println("TArg[" + i + "]: " + targetArgs[i]);
            }

            System.out.println("Entering method: " + methodName);
            System.out.println("Arguments:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("Arg[" + i + "]: " + args[i]);
            }

            if(methodName.contains(targetMethodName) && Arrays.equals(targetArgs, args)){
                isTrigger = true;
            }
        }
    }
}
