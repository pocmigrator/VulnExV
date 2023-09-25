package demo.sootdemo;

import com.google.common.collect.Lists;
import soot.*;
import soot.jimple.toolkits.callgraph.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class CallGraphExample {
    static String basePath = "src/main/java";

    public static void main(String[] args) throws IOException {
        // 设置类路径和其他SOOT选项
        String classPath = "/Users/gaoyi/IdeaProjects/LLMPocMigration/target/classes";
        String mainClassEntry = "sootdemo.callgraph.MyClass";
        String classpath = System.getProperty("java.class.path");
        String javaLibDir = System.getProperty("java.home") + File.separator + "lib" + File.separator;
        String jreDir = javaLibDir + "rt.jar";
        String jceDir = javaLibDir + "jce.jar";
        String path = jreDir + File.pathSeparator + jceDir + File.pathSeparator + classpath;

        // 配置SOOT选项
        soot.options.Options.v().set_app(true);
        soot.options.Options.v().set_whole_program(true);
        soot.options.Options.v().set_no_writeout_body_releasing(true);

        soot.options.Options.v().set_keep_line_number(true); // 保留行号信息，可选
        soot.options.Options.v().set_whole_program(true); // 设置为true以允许整个程序分析
//        soot.options.Options.v().setPhaseOption("cg", "all-reachable:true"); // 设置Call Graph分析选项
        soot.options.Options.v().setPhaseOption("cg", "verbose:false");
        soot.options.Options.v().setPhaseOption("cg", "trim-clinit:true");
        soot.options.Options.v().set_src_prec(soot.options.Options.src_prec_java);
        soot.options.Options.v().set_prepend_classpath(true);
        // don't optimize the program
        soot.options.Options.v().setPhaseOption("wjop", "enabled:false");
        // allow for the absence of some classes
        //Options.v().set_allow_phantom_refs(true);
        soot.options.Options.v().set_exclude(Lists.newArrayList("jdk.*", "apple.laf.*"));
        // 设置类路径
        Scene.v().setSootClassPath(path);

        // 设置main类
        SootClass mainClass = Scene.v().loadClassAndSupport(mainClassEntry);
        Scene.v().setMainClass(mainClass);
        Scene.v().loadNecessaryClasses();
        Scene.v().setEntryPoints(Lists.newArrayList(mainClass.getMethodByName("methodA")));

        // 执行SOOT分析并获取Call Graph
        PackManager.v().runPacks();
        CallGraph callGraph = Scene.v().getCallGraph();

        // 过滤Call Graph，排除Java自身的类
        filterCallGraph(callGraph);

        // 打印Call Graph
//        printCallGraph(callGraph);

        // 转换并保存Call Graph为DOT文件
        String dotGraph = callGraphToDot(callGraph);
        String outputFileName = "callgraph.dot";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            writer.write(dotGraph);
            System.out.println("Call Graph saved as DOT file: " + outputFileName);
        } catch (IOException e) {
            System.err.println("Error while saving DOT file: " + e.getMessage());
        }
    }

    // 辅助方法：将CallGraph对象转换为DOT格式
    private static String callGraphToDot(CallGraph callGraph) {
        StringBuilder dotGraph = new StringBuilder("digraph CallGraph {\n");

        for (Edge edge : callGraph) {
            dotGraph.append("\"").append(edge.src()).append("\" -> \"").append(edge.tgt()).append("\";\n");
        }

        dotGraph.append("}");

        return dotGraph.toString();
    }

    // 自定义的过滤器，用于排除Java自身的类
    private static void filterCallGraph(CallGraph callGraph) {
        List<Edge> removedEdges = Lists.newArrayList();
        System.out.println("original callGraph size: " + callGraph.size());
        for (Edge edge : callGraph) {
            SootClass srcClass = edge.src().getDeclaringClass();
            SootClass tgtClass = edge.tgt().getDeclaringClass();
            boolean isRemoveEdge = isJavaSystemClass(srcClass) || isJavaSystemClass(tgtClass);
            if (isRemoveEdge) {
//                System.out.println("edge removed: "+edge.getSrc().method().getName());
                removedEdges.add(edge);
            }
        }
        if (!removedEdges.isEmpty()) {
            for (Edge e : removedEdges) {
                callGraph.removeEdge(e);
            }
        }
        System.out.println("removed callGraph size: " + callGraph.size());
    }

    // 判断是否为Java系统类
    private static boolean isJavaSystemClass(SootClass sootClass) {
        String className = sootClass.getName();
        List<String> filterPrefixStrings = Lists.newArrayList("java.",
                "javax.", "sun.", "jdk.");
        boolean isJavaSystemClass = filterPrefixStrings.stream().anyMatch(className::startsWith);

        List<String> allPackages = getAllPackages(basePath);

        if(isJavaSystemClass){
            return true;
        }

        boolean isProjectClass = allPackages.stream().anyMatch(className::startsWith);


        return !isProjectClass;
    }

    // 打印Call Graph
    private static void printCallGraph(CallGraph callGraph) {
        Iterator<Edge> it = callGraph.iterator();
        while (it.hasNext()) {
            Edge edge = it.next();
            System.out.println("Edge: " + edge.src() + " -> " + edge.tgt());
        }
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
