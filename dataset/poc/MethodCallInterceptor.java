import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;

import java.util.Arrays;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class MethodCallInterceptor {
    public static boolean isTrigger = false;


    public static void interceptor(Class thdClass, String methodName, Object[] args){
        ByteBuddyAgent.install();
        interceptMethodCalls(thdClass, methodName, args);
    }

    public static void interceptMethodCalls(Class<?> aClass, String methodName, Object[] args) {
        MethodCallLogger.targetMethodName = methodName;
        MethodCallLogger.targetArgs = args;
        try {
            if (!aClass.getName().contains("ByteBuddy")) {
                DynamicType.Loaded<?> load = new ByteBuddy()
                        .redefine(aClass)
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
            if(methodName.contains(targetMethodName) && Arrays.equals(targetArgs, args)){
                isTrigger = true;
            }
        }
    }
}
